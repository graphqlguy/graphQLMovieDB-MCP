package com.graphqlguy.moviedb.review.summary;

import java.time.OffsetDateTime;
import java.util.List;

public record MovieReviewSummary(
    Long movieId,
    int reviewCount,
    Sentiment overallSentiment,
    List<String> themes,
    OffsetDateTime generatedAt
) {}
