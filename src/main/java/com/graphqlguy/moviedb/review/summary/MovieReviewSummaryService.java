package com.graphqlguy.moviedb.review.summary;

import com.graphqlguy.moviedb.review.Review;
import com.graphqlguy.moviedb.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieReviewSummaryService {

    private static final int MIN_REVIEWS_FOR_SUMMARY = 5;

    private final ReviewRepository reviewRepository;

    public MovieReviewSummary summarize(Long movieId) {
        List<Review> reviews = reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
        if (reviews.size() < MIN_REVIEWS_FOR_SUMMARY) {
            return null;
        }
        // Stub summary. A later class wires this to a real summarizer.
        return new MovieReviewSummary(
                movieId,
                reviews.size(),
                Sentiment.MIXED,
                List.of("placeholder theme"),
                OffsetDateTime.now());
    }
}