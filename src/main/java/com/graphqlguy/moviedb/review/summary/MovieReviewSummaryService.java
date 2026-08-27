package com.graphqlguy.moviedb.review.summary;

import com.graphqlguy.moviedb.exception.EntityNotFoundException;
import com.graphqlguy.moviedb.movie.MovieRepository;
import com.graphqlguy.moviedb.review.Review;
import com.graphqlguy.moviedb.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieReviewSummaryService {

    private static final int MIN_REVIEWS_FOR_SUMMARY = 3;

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public MovieReviewSummary summarize(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new EntityNotFoundException("Movie", movieId);
        }
        List<Review> reviews = reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
        if (reviews.isEmpty()) {
            return new MovieReviewSummary(movieId, 0, null, List.of(), null);
        }
        double average = reviews.stream().mapToInt(Review::getScore).average().orElseThrow();
        if (reviews.size() < MIN_REVIEWS_FOR_SUMMARY) {
            return new MovieReviewSummary(movieId, reviews.size(), null, List.of(), average);
        }
        // Stub synthesis. Class 11 replaces this with MCP sampling against the client's model.
        return new MovieReviewSummary(
                movieId,
                reviews.size(),
                "Reviewers broadly agree on this one; see the individual reviews for detail.",
                List.of(),
                average);
    }
}
