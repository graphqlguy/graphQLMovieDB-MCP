package com.graphqlguy.moviedb.review.summary;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MovieReviewSummaryController {

    private final MovieReviewSummaryService summaryService;

    @QueryMapping
    MovieReviewSummary summarizeMovieReviews(@Argument Long movieId) {
        return summaryService.summarize(movieId);
    }
}