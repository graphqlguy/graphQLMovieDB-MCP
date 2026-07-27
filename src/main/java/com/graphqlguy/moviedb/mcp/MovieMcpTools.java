package com.graphqlguy.moviedb.mcp;

// Boot 4 ships Jackson 3: the auto-configured mapper lives in tools.jackson.*,
// not com.fasterxml.jackson.* (which Boot 3-era material shows).
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.graphqlguy.moviedb.recommendation.Mood;
import com.graphqlguy.moviedb.review.Review;
import com.graphqlguy.moviedb.review.summary.Sentiment;
import com.graphqlguy.moviedb.watchlist.WatchStatus;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageResult;
import io.modelcontextprotocol.spec.McpSchema.ElicitResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
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
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class 7: each curated GraphQL operation becomes one @McpTool method. Every
 * method has the same shape: typed input record, operation text held as a
 * constant in {@link MovieOperations}, an ExecutionGraphQlRequest executed
 * in-process through ExecutionGraphQlService, and a Jackson convertValue into
 * a typed record.
 */
@Service
public class MovieMcpTools {

    private static final int MIN_REVIEWS_FOR_SUMMARY = 5;

    private final ExecutionGraphQlService graphql;
    private final ObjectMapper objectMapper;
    private final ToolResults toolResults;
    private final ServerSideSummarizer serverSideSummarizer = new ServerSideSummarizer();

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

    public record WatchlistSubject(
            String movieId,
            String tvShowId
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
                description = "Recommendation input: mood (one of COMFORT, ADVENTURE, ROMANCE, HORROR, THOUGHTFUL, COMEDIC) and an optional excludeWatched flag, default false, which filters out movies the signed-in user has already marked WATCHED on their watchlist.",
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
            // Class 10: both special parameters are filled in by the framework
            // and never appear in the tool's input schema. context.progress(...)
            // reads the client's token on its own and quietly does nothing when
            // the client did not send one.
            McpSyncRequestContext context,
            @McpProgressToken String progressToken,
            @McpToolParam(description = "Movie ID, as it appears in the schema.", required = true)
            String movieId) {

        context.progress(p -> p.progress(0.0).total(1.0).message("Loading reviews"));

        List<Review> reviews = loadReviews(movieId);
        if (reviews.size() < MIN_REVIEWS_FOR_SUMMARY) {
            return null;
        }

        context.progress(p -> p.progress(0.4).total(1.0).message("Summarizing"));

        MovieReviewSummary summary = summarizeWithSamplingOrFallback(context, movieId, reviews);

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

    // Class 11: borrow the client's model through sampling when the capability
    // was negotiated; otherwise fall back to the server-side summarizer so the
    // tool works against clients that never heard of sampling.
    private MovieReviewSummary summarizeWithSamplingOrFallback(
            McpSyncRequestContext context, String movieId, List<Review> reviews) {

        String prompt = buildSummarizationPrompt(reviews);

        if (context.sampleEnabled()) {
            CreateMessageResult result = context.sample(s -> s
                .message(prompt)
                .systemPrompt("You summarize movie reviews into themes and an overall sentiment.")
                .maxTokens(1024));
            String text = result.content() instanceof TextContent tc ? tc.text() : "";
            return parseSummary(movieId, reviews.size(), text);
        }
        return serverSideSummarizer.summarize(movieId, reviews);
    }

    // Lays out each review's 1-to-10 score plus its comment, so the model judges
    // sentiment against the same scale the schema documents for Review.score,
    // and pins a reply format parseSummary can read back.
    private String buildSummarizationPrompt(List<Review> reviews) {
        StringBuilder prompt = new StringBuilder("""
            Summarize the following movie reviews. Scores are integers from 1 (worst)
            to 10 (best). Reply with exactly two lines:
            SENTIMENT: POSITIVE | MIXED | NEGATIVE
            THEMES: theme one; theme two; theme three

            Reviews:
            """);
        for (Review review : reviews) {
            prompt.append("- score ").append(review.getScore());
            if (review.getComment() != null && !review.getComment().isBlank()) {
                prompt.append(": ").append(review.getComment());
            }
            prompt.append('\n');
        }
        return prompt.toString();
    }

    private MovieReviewSummary parseSummary(String movieId, int reviewCount, String text) {
        Sentiment sentiment = Sentiment.MIXED;
        List<String> themes = List.of();
        for (String line : text.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.regionMatches(true, 0, "SENTIMENT:", 0, 10)) {
                String value = trimmed.substring(10).trim().toUpperCase();
                try {
                    sentiment = Sentiment.valueOf(value);
                } catch (IllegalArgumentException ignored) {
                    // keep the MIXED default when the model strays from the format
                }
            } else if (trimmed.regionMatches(true, 0, "THEMES:", 0, 7)) {
                themes = java.util.Arrays.stream(trimmed.substring(7).split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            }
        }
        return new MovieReviewSummary(movieId, reviewCount, sentiment, themes, OffsetDateTime.now());
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
