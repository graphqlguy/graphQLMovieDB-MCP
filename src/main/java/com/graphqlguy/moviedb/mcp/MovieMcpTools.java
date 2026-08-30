package com.graphqlguy.moviedb.mcp;

// Boot 4 ships Jackson 3: the auto-configured mapper lives in tools.jackson.*,
// not com.fasterxml.jackson.* (which Boot 3-era material shows).
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.graphqlguy.moviedb.recommendation.Mood;
import com.graphqlguy.moviedb.review.Review;
import com.graphqlguy.moviedb.watchlist.WatchStatus;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ElicitResult;
import org.springframework.ai.mcp.annotation.McpProgressToken;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.ai.mcp.annotation.context.StructuredElicitResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.ExecutionGraphQlRequest;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.support.DefaultExecutionGraphQlRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * Class 7: each curated GraphQL operation becomes one @McpTool method. Every
 * method has the same shape: typed input record, operation text held as a
 * constant in {@link MovieOperations}, an ExecutionGraphQlRequest executed
 * in-process through ExecutionGraphQlService, and a Jackson convertValue into
 * a typed record.
 */
@Service
public class MovieMcpTools {

    private static final int MIN_REVIEWS_FOR_SUMMARY = 3;

    private final ExecutionGraphQlService graphql;
    private final ObjectMapper objectMapper;
    private final ToolResults toolResults;
    private final SamplingReviewSummarizer samplingReviewSummarizer;

    @Autowired
    public MovieMcpTools(
            ExecutionGraphQlService graphql, ObjectMapper objectMapper, ToolResults toolResults,
            SamplingReviewSummarizer samplingReviewSummarizer) {
        this.graphql = graphql;
        this.objectMapper = objectMapper;
        this.toolResults = toolResults;
        this.samplingReviewSummarizer = samplingReviewSummarizer;
    }

    // Convenience constructors used by unit tests: a real ObjectMapper, a
    // ToolResults built on it, and a SamplingReviewSummarizer are all cheap
    // and exercise the production paths.
    public MovieMcpTools(ExecutionGraphQlService graphql, ObjectMapper objectMapper) {
        this(graphql, objectMapper, new ToolResults(objectMapper), new SamplingReviewSummarizer());
    }

    public MovieMcpTools(ExecutionGraphQlService graphql) {
        this(graphql, new ObjectMapper());
    }

    public record RecommendInput(
            Mood mood,
            boolean excludeWatched
    ) {}

    public record MovieSummary(
            String id,
            String title,
            Integer releaseYear,
            String genre,
            Double rating
    ) {}

    public record MovieReviewSummary(
            String movieId,
            Integer reviewCount,
            @Nullable String summary,
            List<String> themes,
            @Nullable Double averageScore
    ) {}

    // @Nullable on both components keeps them out of the generated schema's
    // required list; the exactly-one rule is enforced by GraphQL's @oneOf and
    // advertised in the tool description.
    public record WatchlistSubject(
            @Nullable String movieId,
            @Nullable String tvShowId
    ) {}

    public record AddedContent(String title) {}

    public record WatchlistItemSummary(
            String id,
            WatchStatus status,
            AddedContent content
    ) {}

    @McpTool(
        name = "recommendMoviesForMood",
        description = """
            Recommend movies that fit a given mood. Suitable for low-stakes
            recommendation flows where an agent is asking on behalf of a user.
            Results are ranked by rating, highest first, with a stable
            tiebreak on id, so repeated calls return the same order and an
            agent that retries does not see the list reshuffle under it.
            """,
        annotations = @McpTool.McpAnnotations(
            title = "Recommend Movies For Mood",
            readOnlyHint = true,
            destructiveHint = false,
            idempotentHint = true,
            openWorldHint = false))
    // Class 15: reads require movies:read. SCOPE_ is Spring Security's prefix
    // for OAuth scopes mapped from the JWT scope claim.
    @PreAuthorize("hasAuthority('SCOPE_movies:read')")
    public List<MovieSummary> recommendMoviesForMood(
            @McpToolParam(
                description = "Recommendation input: mood (one of COMFORT, ADVENTURE, ROMANCE, HORROR, THOUGHTFUL, COMEDY) and an excludeWatched flag, which the input schema requires on every call. Send false unless the user wants movies they have already marked WATCHED on their watchlist filtered out.",
                required = true)
            RecommendInput input) {

        Map<String, Object> variables = Map.of(
            "mood", input.mood().name(),
            "excludeWatched", input.excludeWatched());

        ExecutionGraphQlResponse response = executeOperation(
            "RecommendMoviesForMood",
            MovieOperations.RECOMMEND_MOVIES_FOR_MOOD,
            variables);

        Object value = response.field("recommendMoviesForMood").getValue();
        return objectMapper.convertValue(value, new TypeReference<List<MovieSummary>>() {});
    }

    @McpTool(
        name = "summarizeMovieReviews",
        description = """
            Summarize user reviews for a specific movie. Always returns a
            summary object; when there are fewer than three reviews the
            summary field is null and the agent should read reviewCount to
            see how many there were. Use this only after the user has
            identified a movie they care about; do not call speculatively
            across many movies.
            """,
        annotations = @McpTool.McpAnnotations(
            title = "Summarize Movie Reviews",
            readOnlyHint = true,
            destructiveHint = false,
            idempotentHint = true,
            openWorldHint = false))
    @PreAuthorize("hasAuthority('SCOPE_movies:read') and hasAuthority('SCOPE_reviews:read')")
    public MovieReviewSummary summarizeMovieReviews(
            // Class 10: both special parameters are filled in by the framework
            // and never appear in the tool's input schema. context.progress(...)
            // reads the client's token on its own and quietly no-ops when
            // the client did not send one.
            McpSyncRequestContext context,
            @McpProgressToken String progressToken,
            @McpToolParam(description = "Movie ID, as it appears in the schema.", required = true)
            String movieId) {

        context.progress(p -> p.progress(0.0).total(1.0).message("Loading reviews"));

        List<Review> reviews = loadReviews(movieId);
        if (reviews.isEmpty()) {
            return new MovieReviewSummary(movieId, 0, null, List.of(), null);
        }
        double averageScore = reviews.stream().mapToInt(Review::getScore).average().orElseThrow();
        if (reviews.size() < MIN_REVIEWS_FOR_SUMMARY) {
            return new MovieReviewSummary(movieId, reviews.size(), null, List.of(), averageScore);
        }

        context.progress(p -> p.progress(0.4).total(1.0).message("Summarizing"));

        MovieReviewSummary summary = samplingReviewSummarizer.summarize(context, movieId, reviews, averageScore);

        context.progress(p -> p.progress(1.0).total(1.0).message("Done"));

        return summary;
    }

    // Class 11: the raw material for the summary, read through the same
    // in-process GraphQL engine as everything else.
    private List<Review> loadReviews(String movieId) {
        ExecutionGraphQlResponse response = executeOperation(
            "MovieReviews",
            MovieOperations.MOVIE_REVIEWS,
            Map.of("movieId", movieId));
        Object value = response.field("movie.reviews").getValue();
        if (value == null) {
            return List.of();
        }
        return objectMapper.convertValue(value, new TypeReference<List<Review>>() {});
    }

    @McpTool(
        name = "addToWatchlist",
        description = """
            Add a movie or TV show to the signed-in user's watch list. Provide
            exactly one of subject.movieId or subject.tvShowId, never both.
            Status is optional; when omitted the server records WANT_TO_WATCH.
            Adding a title that is already on the list fails with an "already
            in your watch list" error; treat that as already done, not as a
            failure worth retrying.
            """,
        annotations = @McpTool.McpAnnotations(
            title = "Add To Watchlist",
            readOnlyHint = false,
            destructiveHint = false,
            idempotentHint = true,
            openWorldHint = false))
    // Class 15: a write to user-owned data requires watchlist:write.
    @PreAuthorize("hasAuthority('SCOPE_watchlist:write')")
    public CallToolResult addToWatchlist(
            McpSyncRequestContext context,
            @McpToolParam(description = "The title to add: exactly one of movieId or tvShowId.", required = true)
            WatchlistSubject subject,
            @McpToolParam(description = "Optional initial status, WANT_TO_WATCH or WATCHED. Omit for the default WANT_TO_WATCH.", required = false)
            WatchStatus status) {

        // @oneOf is strict about presence, not merely value: an explicit null
        // entry still counts as a provided field, so the absent field must be
        // absent from the variables map entirely.
        Map<String, Object> subjectMap = new HashMap<>();
        if (subject.movieId() != null) {
            subjectMap.put("movieId", subject.movieId());
        }
        if (subject.tvShowId() != null) {
            subjectMap.put("tvShowId", subject.tvShowId());
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("subject", subjectMap);
        WatchStatus resolved = resolveStatus(context, status);
        if (resolved != null) {
            variables.put("status", resolved.name());
        }

        // Class 9: execute leniently and let ToolResults decide how the outcome
        // reaches the agent (isError, structuredContent, compatibility text).
        ExecutionGraphQlResponse response = executeOperationLenient(
            "AddToWatchlist",
            MovieOperations.ADD_TO_WATCHLIST,
            variables);

        return toolResults.toCallResult(response, "addToWatchlist");
    }

    /**
     * Class 11: three branches. If the agent supplied a status, respect it. If
     * not and the client cannot elicit, resolve to null so the variable is
     * omitted and the WatchlistService records its WANT_TO_WATCH default. If
     * the client can elicit, ask the one person who knows. Decline, cancel, no
     * capability, and an empty form all converge on the same fallback; the
     * mutation proceeds either way, because the user asked for the add.
     */
    private WatchStatus resolveStatus(McpSyncRequestContext context, WatchStatus status) {
        if (status != null) {
            return status;
        }
        if (!context.elicitEnabled()) {
            return null; // omit the variable; the service records WANT_TO_WATCH
        }
        StructuredElicitResult<WatchStatusRequest> result = context.elicit(
            e -> e.message("Have you already watched this title, or is it one to watch later?"),
            WatchStatusRequest.class);

        if (result.action() != ElicitResult.Action.ACCEPT
                || result.structuredContent() == null
                || result.structuredContent().status() == null) {
            return null; // decline, cancel, or an empty form: same fallback
        }
        return result.structuredContent().status();
    }

    // Fail fast (Class 7 policy, kept for the read tools): throw a runtime
    // exception when the response has errors and let the MCP runtime wrap it as
    // a tool error.
    private ExecutionGraphQlResponse executeOperation(
            String operationName, String document, Map<String, Object> variables) {
        ExecutionGraphQlResponse response = executeOperationLenient(operationName, document, variables);
        if (response != null && !response.getErrors().isEmpty()) {
            throw new IllegalStateException(
                "GraphQL execution returned errors: " + response.getErrors());
        }
        return response;
    }

    // Class 9: no error policy of its own; the caller routes the response
    // through ToolResults.toCallResult.
    //
    // Class 19 fix: graphql.execute(...) starts graphql-java's own
    // CompletableFuture-based engine directly; it never subscribes through a
    // Reactor pipeline tied to this call's thread, so a plain
    // Mono.contextWrite(...) around it never reaches the field resolvers.
    // Spring for GraphQL's own request-scoped propagation instead reads the
    // ExecutionInput's GraphQLContext: on every field it takes a fresh
    // snapshot of whichever registered thread-local values that GraphQLContext
    // carries (keyed by each accessor's key, here
    // SecurityContext.class.getName(), the same key
    // SecurityContextThreadLocalAccessor uses) and restores them around the
    // resolver call, on whatever thread actually runs it. Seeding that same
    // key with this call's SecurityContext before execution starts is what
    // carries this thread's Authentication down to WatchlistController's
    // Principal argument and its @PreAuthorize("isAuthenticated()"), instead
    // of the anonymous context they saw before. An unauthenticated caller is
    // still refused earlier, at the tool-level @PreAuthorize checked below,
    // so there is nothing to seed in that case.
    private ExecutionGraphQlResponse executeOperationLenient(
            String operationName, String document, Map<String, Object> variables) {
        ExecutionGraphQlRequest request = new DefaultExecutionGraphQlRequest(
                document, operationName, variables, Map.of(),
                UUID.randomUUID().toString(), null);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            request.configureExecutionInput((executionInput, builder) -> {
                executionInput.getGraphQLContext().put(SecurityContext.class.getName(), securityContext);
                return executionInput;
            });
        }
        return graphql.execute(request).block();
    }
}
