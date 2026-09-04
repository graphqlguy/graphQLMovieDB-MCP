package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.review.Review;
import io.modelcontextprotocol.spec.McpSchema.CreateMessageResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Class 11: borrows the client's model through sampling when the capability
 * was negotiated; otherwise falls back to the server-side summarizer so the
 * tool works against clients that never heard of sampling. The sampling call
 * is slow and costs money, so summarize is cached for a short window, keyed
 * on the movie id, and a repeat call for the same movie skips it entirely.
 *
 * This lives in its own bean instead of as a private method on MovieMcpTools
 * for two reasons. First, @Cacheable only intercepts calls that arrive
 * through the Spring AOP proxy; a call made through `this` inside the same
 * bean bypasses the proxy and the annotation would not do anything, and a
 * private helper called from the tool method would have exactly that shape.
 * Second, MovieMcpTools is the bean SyncMcpToolProvider reflects over to find
 * @McpTool methods, and once Spring proxies a bean (which a @Cacheable
 * method on it would force), the proxy's overriding methods stop carrying
 * the original method annotations, so the tool scanner would stop finding
 * recommendMoviesForMood, summarizeMovieReviews, and addToWatchlist.
 * Keeping MovieMcpTools itself free of @Cacheable methods keeps it
 * unproxied, and the caching lives on this collaborator instead, called from
 * MovieMcpTools the ordinary way, through the injected reference.
 */
@Service
public class SamplingReviewSummarizer {

    private final ServerSideSummarizer serverSideSummarizer = new ServerSideSummarizer();

    @Cacheable(value = "reviewSummaries", key = "#movieId")
    public MovieMcpTools.MovieReviewSummary summarize(
            McpSyncRequestContext context, String movieId, List<Review> reviews, double averageScore) {

        String prompt = buildSummarizationPrompt(reviews);

        if (context.sampleEnabled()) {
            CreateMessageResult result = context.sample(s -> s
                .message(prompt)
                .systemPrompt("You summarize movie reviews into a few sentences and recurring themes.")
                .maxTokens(1024));
            String text = result.content() instanceof TextContent tc ? tc.text() : "";
            return parseSummary(movieId, reviews.size(), averageScore, text);
        }
        return serverSideSummarizer.summarize(movieId, reviews, averageScore);
    }

    // Lays out each review's 1-to-10 score plus its comment, so the model can
    // ground its synthesis against the same scale the schema documents for
    // Review.score, and pins a reply format parseSummary can read back.
    private String buildSummarizationPrompt(List<Review> reviews) {
        StringBuilder prompt = new StringBuilder("""
            Summarize the following movie reviews. Scores are integers from 1 (worst)
            to 10 (best). Reply with exactly two lines:
            SUMMARY: one or two sentences synthesizing what reviewers said
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

    private MovieMcpTools.MovieReviewSummary parseSummary(
            String movieId, int reviewCount, double averageScore, String text) {
        String summary = null;
        List<String> themes = List.of();
        for (String line : text.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.regionMatches(true, 0, "SUMMARY:", 0, 8)) {
                summary = trimmed.substring(8).trim();
            } else if (trimmed.regionMatches(true, 0, "THEMES:", 0, 7)) {
                themes = Arrays.stream(trimmed.substring(7).split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            }
        }
        return new MovieMcpTools.MovieReviewSummary(movieId, reviewCount, summary, themes, averageScore);
    }
}
