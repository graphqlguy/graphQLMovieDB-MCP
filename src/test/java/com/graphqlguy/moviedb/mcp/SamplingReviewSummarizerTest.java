package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.mcp.MovieMcpTools.MovieReviewSummary;
import com.graphqlguy.moviedb.review.Review;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.mcp.annotation.context.McpRequestContextTypes.SamplingSpec;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import reactor.core.Exceptions;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Class 11: the sampling fallback. When a client does not answer a sampling
 * request, sample(...) throws once spring.ai.mcp.server.request-timeout has
 * passed, and the summarizer answers with the server-side summary. Any other
 * exception is a bug, so it has to reach the caller.
 */
class SamplingReviewSummarizerTest {

    private final List<Review> reviews = List.of(
        Review.builder().score(9).comment("Beautiful cinematography").build(),
        Review.builder().score(8).comment("Charming characters").build(),
        Review.builder().score(7).comment("A cozy watch").build());

    @Test
    void summarize_whenSamplingTimesOut_shouldReturnServerSideSummary() {
        // The exception the blocking sample(...) call throws when the SDK's
        // timeout fires: Reactor wraps the checked TimeoutException.
        McpSyncRequestContext context = samplingContextThrowing(
            Exceptions.propagate(new TimeoutException("Did not observe any item or terminal signal within 20000ms")));

        MovieReviewSummary summary = new SamplingReviewSummarizer().summarize(context, "1", reviews, 8.0);

        assertThat(summary).isEqualTo(new ServerSideSummarizer().summarize("1", reviews, 8.0));
    }

    @Test
    void summarize_whenSamplingFailsWithAnotherException_shouldThrowIt() {
        McpSyncRequestContext context = samplingContextThrowing(new IllegalStateException("a bug in the tool"));

        assertThatThrownBy(() -> new SamplingReviewSummarizer().summarize(context, "1", reviews, 8.0))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("a bug in the tool");
    }

    private McpSyncRequestContext samplingContextThrowing(RuntimeException exception) {
        McpSyncRequestContext context = Mockito.mock(McpSyncRequestContext.class);
        Mockito.when(context.sampleEnabled()).thenReturn(true);
        // sample(...) is overloaded; the type witness picks the Consumer
        // overload that the summarizer calls. thenAnswer throws the exception
        // object as it is. thenThrow would throw what fillInStackTrace()
        // returns, and Reactor's wrapper returns its TimeoutException cause
        // there, so the test would see a different exception than production.
        Mockito.when(context.sample(Mockito.<Consumer<SamplingSpec>>any())).thenAnswer(invocation -> {
            throw exception;
        });
        return context;
    }
}
