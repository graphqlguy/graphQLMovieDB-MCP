package com.graphqlguy.moviedb.review.summary;

import java.util.List;

public record MovieReviewSummary(
        Long movieId,
        int reviewCount,
        String summary,
        List<String> themes,
        Double averageScore
) {}
