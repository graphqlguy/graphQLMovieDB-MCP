package com.graphqlguy.moviedb.mcp;

// Boot 4 ships Jackson 3: the auto-configured mapper lives in tools.jackson.*,
// not com.fasterxml.jackson.* (which Boot 3-era material shows).
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.graphqlguy.moviedb.recommendation.Mood;
import com.graphqlguy.moviedb.review.summary.Sentiment;
import com.graphqlguy.moviedb.watchlist.WatchStatus;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.ExecutionGraphQlRequest;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.support.DefaultExecutionGraphQlRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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

    private final ExecutionGraphQlService graphql;
    private final ObjectMapper objectMapper;
    private final ToolResults toolResults;

    @Autowired
    public MovieMcpTools(ExecutionGraphQlService graphql, ObjectMapper objectMapper, ToolResults toolResults) {
        this.graphql = graphql;
        this.objectMapper = objectMapper;
        this.toolResults = toolResults;
    }

    // Convenience constructors used by unit tests: a real ObjectMapper and a
    // ToolResults built on it are cheap and exercise the production paths.
    public MovieMcpTools(ExecutionGraphQlService graphql, ObjectMapper objectMapper) {
        this(graphql, objectMapper, new ToolResults(objectMapper));
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
            Double averageRating
    ) {}

    public record MovieReviewSummary(
            String movieId,
            Integer reviewCount,
            Sentiment overallSentiment,
            List<String> themes,
            OffsetDateTime generatedAt
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
            Results are deterministic within a 60-second window so an agent
            that retries does not see the catalog shuffle under it.
            """,
        annotations = @McpTool.McpAnnotations(
            title = "Recommend Movies For Mood",
            readOnlyHint = true,
            destructiveHint = false,
            idempotentHint = true,
            openWorldHint = false
        )
    )
    public List<MovieSummary> recommendMoviesForMood(
            @McpToolParam(
                description = "Recommendation input: mood (one of COMFORT, ADVENTURE, ROMANCE, HORROR, THOUGHTFUL, COMEDIC) and an excludeWatched flag, which the input schema requires on every call. Send false unless the user wants movies they have already marked WATCHED on their watchlist filtered out.",
                required = true
            )
            RecommendInput input) {

        Map<String, Object> variables = Map.of(
            "mood", input.mood().name(),
            "excludeWatched", input.excludeWatched()
        );

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
            Summarize user reviews for a specific movie. Returns null when there
            are not enough reviews to generate a meaningful summary (currently
            five). Agents should treat null as "not enough data," not "no
            reviews exist." Use this only after the user has identified a movie
            they care about; do not call speculatively across many movies.
            """,
        annotations = @McpTool.McpAnnotations(
            title = "Summarize Movie Reviews",
            readOnlyHint = true,
            destructiveHint = false,
            idempotentHint = true,
            openWorldHint = false
        )
    )
    public MovieReviewSummary summarizeMovieReviews(
            @McpToolParam(description = "Movie ID, as it appears in the schema.", required = true)
            String movieId) {

        ExecutionGraphQlResponse response = executeOperation(
            "SummarizeMovieReviews",
            MovieOperations.SUMMARIZE_MOVIE_REVIEWS,
            Map.of("movieId", movieId));

        Object value = response.field("summarizeMovieReviews").getValue();
        return objectMapper.convertValue(value, MovieReviewSummary.class);
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
            openWorldHint = false
        )
    )
    public CallToolResult addToWatchlist(
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
        if (status != null) {
            variables.put("status", status.name());
        }

        // Class 9: execute leniently and let ToolResults decide how the outcome
        // reaches the agent (isError, structuredContent, compatibility text).
        ExecutionGraphQlResponse response = executeOperationLenient(
            "AddToWatchlist",
            MovieOperations.ADD_TO_WATCHLIST,
            variables);

        return toolResults.toCallResult(response, "addToWatchlist");
    }

    // Fail fast (Class 7 policy, kept for the read tools): throw a runtime
    // exception when the response has errors and let the MCP runtime wrap it as
    // a tool error.
    private ExecutionGraphQlResponse executeOperation(
            String operationName, String document, Map<String, Object> variables) {
        ExecutionGraphQlResponse response = executeOperationLenient(operationName, document, variables);
        if (response != null && response.getErrors() != null && !response.getErrors().isEmpty()) {
            throw new IllegalStateException(
                "GraphQL execution returned errors: " + response.getErrors());
        }
        return response;
    }

    // Class 9: no error policy of its own; the caller routes the response
    // through ToolResults.toCallResult.
    private ExecutionGraphQlResponse executeOperationLenient(
            String operationName, String document, Map<String, Object> variables) {
        ExecutionGraphQlRequest request = new DefaultExecutionGraphQlRequest(
                document, operationName, variables, Map.of(),
                UUID.randomUUID().toString(), null);
        return graphql.execute(request).block();
    }
}
