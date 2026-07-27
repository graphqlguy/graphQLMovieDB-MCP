package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.mcp.MovieMcpTools.MovieReviewSummary;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.mcp.annotation.context.McpRequestContextTypes.ProgressSpec;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class SummarizeProgressTest {

    @Test
    void summarizeMovieReviews_returnsSummary() {
        MovieMcpTools tools = withMockedDependencies();
        McpSyncRequestContext context = Mockito.mock(McpSyncRequestContext.class);

        MovieReviewSummary summary =
            tools.summarizeMovieReviews(context, "progress-token-1", "1");

        assertThat(summary.reviewCount()).isGreaterThanOrEqualTo(5);
        // progress() is overloaded (Consumer vs ProgressNotification); the type
        // witness pins the matcher to the Consumer overload the tool actually calls.
        Mockito.verify(context, Mockito.atLeastOnce())
            .progress(Mockito.<Consumer<ProgressSpec>>any());
    }

    /**
     * Same Mockito setup as the unit test: a mocked ExecutionGraphQlService
     * returning a canned review list, paired with a real ObjectMapper. The
     * mocked context makes every context.progress(...) call a recorded no-op,
     * mirroring production behavior when a client did not send a progress
     * token, and sampleEnabled() defaults to false so the server-side
     * summarizer runs.
     */
    private MovieMcpTools withMockedDependencies() {
        List<Map<String, Object>> cannedReviews = List.of(
            Map.of("score", 9, "comment", "Beautiful cinematography and a warm story"),
            Map.of("score", 8, "comment", "Charming characters"),
            Map.of("score", 7, "comment", "A cozy watch"),
            Map.of("score", 9, "comment", "Gorgeous soundtrack"),
            Map.of("score", 8, "comment", "Feel-good from start to finish")
        );
        return new MovieMcpTools(MockGraphQlServiceFactory.returning(cannedReviews), new ObjectMapper());
    }
}
