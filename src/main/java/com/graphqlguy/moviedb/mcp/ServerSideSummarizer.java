package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.review.Review;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Class 11: the fallback summarizer used when the client did not negotiate the
 * sampling capability. The summary line comes from integer score bands on the
 * 1-to-10 scale the schema documents for Review.score; themes are the most
 * frequent meaningful words across the comments. Deliberately simple: the
 * interesting path is sampling, and this one only has to be honest.
 */
public class ServerSideSummarizer {

    private static final Set<String> STOP_WORDS = Set.of(
        "the", "and", "was", "this", "that", "with", "for", "movie", "film",
        "but", "not", "its", "very", "just", "have", "has", "are", "you");

    public MovieMcpTools.MovieReviewSummary summarize(String movieId, List<Review> reviews, double averageScore) {
        String summary = averageScore > 7.0
            ? "Reviewers respond favorably overall, averaging " + Math.round(averageScore * 10.0) / 10.0 + " out of 10."
            : averageScore < 5.0
            ? "Reviewers respond unfavorably overall, averaging " + Math.round(averageScore * 10.0) / 10.0 + " out of 10."
            : "Reviewers are split, averaging " + Math.round(averageScore * 10.0) / 10.0 + " out of 10.";
        return new MovieMcpTools.MovieReviewSummary(
            movieId, reviews.size(), summary, themes(reviews), averageScore);
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
