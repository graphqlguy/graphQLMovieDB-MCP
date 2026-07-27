package com.graphqlguy.moviedb.mcp;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.support.DefaultExecutionGraphQlRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class 16: stage-and-confirm for a destructive mutation. The first call stages
 * the action and returns a single-use, expiring confirmation token bound to the
 * authenticated principal; the second call executes it. The pattern makes the
 * destructive action visible, two-step, expiring, and auditable. Server-enforced
 * human approval would use elicitation (Class 11); this bounds blast radius and
 * leans on the host's approval UI for the human gate.
 */
@Service
public class SafeMutationTools {

    private final Map<String, PendingAction> pending = new ConcurrentHashMap<>();
    private final ExecutionGraphQlService graphql;

    public SafeMutationTools(ExecutionGraphQlService graphql) {
        this.graphql = graphql;
    }

    public record PendingAction(
        String tool, Map<String, Object> args, String principal, Instant expiresAt) {}

    @McpTool(
        name = "stageRemoveFromWatchlist",
        description = """
            Stage removal of an item from the signed-in user's watch list.
            Returns a confirmation token. The actual removal only happens when
            the user calls confirmAction with the returned token within
            60 seconds. Tokens are single-use.
            """,
        annotations = @McpTool.McpAnnotations(
            title = "Stage Remove From Watchlist",
            readOnlyHint = false,
            destructiveHint = true,
            idempotentHint = false,
            openWorldHint = false
        )
    )
    @PreAuthorize("hasAuthority('SCOPE_watchlist:write')")
    public Map<String, Object> stageRemoveFromWatchlist(
            @McpToolParam(required = true) String itemId) {
        String token = UUID.randomUUID().toString();
        pending.put(token, new PendingAction(
            "removeFromWatchlist",
            Map.of("itemId", itemId),
            currentPrincipal(),
            Instant.now().plusSeconds(60)));
        return Map.of(
            "confirmationToken", token,
            "previewMessage", "Will remove watchlist item " + itemId + ". Confirm with confirmAction.");
    }

    @McpTool(
        name = "confirmAction",
        description = """
            Execute a previously staged action by confirmation token. Single-use.
            Tokens expire 60 seconds after staging.
            """
    )
    @PreAuthorize("hasAuthority('SCOPE_watchlist:write')")
    public Map<String, Object> confirmAction(@McpToolParam(required = true) String confirmationToken) {
        PendingAction action = pending.remove(confirmationToken);
        if (action == null) {
            return Map.of("status", "error", "message", "Token not found or already used.");
        }
        if (!action.principal().equals(currentPrincipal())) {
            return Map.of("status", "error", "message", "Token was staged by a different user.");
        }
        if (action.expiresAt().isBefore(Instant.now())) {
            return Map.of("status", "error", "message", "Token expired.");
        }
        return executeStaged(action);
    }

    @Scheduled(fixedDelay = 60_000)
    void sweepExpired() {
        Instant now = Instant.now();
        pending.values().removeIf(action -> action.expiresAt().isBefore(now));
    }

    private String currentPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "anonymous" : auth.getName();
    }

    // Runs the real removeFromWatchlist mutation in-process. The propagated
    // security context meets the same guards every GraphQL client meets:
    // WatchlistController requires an authenticated caller and WatchlistService
    // verifies the item belongs to that caller before touching it.
    private Map<String, Object> executeStaged(PendingAction action) {
        var request = new DefaultExecutionGraphQlRequest(
            MovieOperations.REMOVE_FROM_WATCHLIST, "RemoveFromWatchlist",
            action.args(), Map.of(), UUID.randomUUID().toString(), null);
        ExecutionGraphQlResponse response = graphql.execute(request).block();
        if (response != null && response.getErrors() != null && !response.getErrors().isEmpty()) {
            return Map.of("status", "error",
                "message", "Removal failed: " + response.getErrors());
        }
        Object removed = response == null ? null : response.field("removeFromWatchlist").getValue();
        return Map.of("status", "ok", "executed", action.tool(), "removed", removed == null ? false : removed);
    }
}
