package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.review.Review;
import com.graphqlguy.moviedb.review.summary.Sentiment;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Class 11: the fallback summarizer used when the client did not negotiate the
 * sampling capability. Sentiment comes from integer score bands on the 1-to-10
 * scale the schema documents for Review.score; themes are the most frequent
 * meaningful words across the comments. Deliberately simple: the interesting
 * path is sampling, and this one only has to be honest.
 */
public class ServerSideSummarizer {

    private static final Set<String> STOP_WORDS = Set.of(
        "the", "and", "was", "this", "that", "with", "for", "movie", "film",
        "but", "not", "its", "very", "just", "have", "has", "are", "you");

    public MovieMcpTools.MovieReviewSummary summarize(String movieId, List<Review> reviews) {
        double mean = reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
        Sentiment sentiment = mean > 7.0 ? Sentiment.POSITIVE
            : mean < 5.0 ? Sentiment.NEGATIVE
            : Sentiment.MIXED;
        return new MovieMcpTools.MovieReviewSummary(
            movieId, reviews.size(), sentiment, themes(reviews), OffsetDateTime.now());
    }

    private List<String> themes(List<Review> reviews) {
        Set<String> themes = new LinkedHashSet<>();
        for (Review review : reviews) {
            if (review.getComment() == null) continue;
            for (String word : review.getComment().toLowerCase(Locale.ROOT).split("[^a-z]+")) {
                if (word.length() > 3 && !STOP_WORDS.contains(word)) {
                    themes.add(word);
                }
                if (themes.size() >= 5) return List.copyOf(themes);
            }
        }
        return List.copyOf(themes);
    }
}
