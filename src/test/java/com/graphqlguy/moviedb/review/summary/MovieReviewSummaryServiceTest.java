package com.graphqlguy.moviedb.review.summary;

import com.graphqlguy.moviedb.exception.EntityNotFoundException;
import com.graphqlguy.moviedb.movie.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MovieReviewSummaryServiceTest {

    @Autowired
    private MovieReviewSummaryService service;

    @Autowired
    private MovieRepository movieRepository;

    private Long shawshankId;
    private Long godfatherId;
    private Long movieWithNoReviewsId;

    @BeforeEach
    void resolveMovieIds() {
        shawshankId = idForTitle("The Shawshank Redemption");
        godfatherId = idForTitle("The Godfather");
        movieWithNoReviewsId = idForTitle("Forrest Gump");
    }

    private Long idForTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
            .filter(movie -> movie.getTitle().equalsIgnoreCase(title))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No seeded movie titled " + title))
            .getId();
    }

    @Test
    void manyReviewsProduceAProseSummary() {
        MovieReviewSummary s = service.summarize(shawshankId);
        assertThat(s.reviewCount()).isGreaterThanOrEqualTo(3);
        assertThat(s.summary()).isNotBlank();
        assertThat(s.averageScore()).isNotNull();
    }

    @Test
    void tooFewReviewsOmitTheSummaryButKeepTheScore() {
        MovieReviewSummary s = service.summarize(godfatherId);
        assertThat(s.reviewCount()).isBetween(1, 2);
        assertThat(s.summary()).isNull();
        assertThat(s.averageScore()).isNotNull();
    }

    @Test
    void noReviewsOmitBothSummaryAndScore() {
        MovieReviewSummary s = service.summarize(movieWithNoReviewsId);
        assertThat(s.reviewCount()).isZero();
        assertThat(s.summary()).isNull();
        assertThat(s.averageScore()).isNull();
    }

    @Test
    void unknownMovieRaisesNotFound() {
        assertThatThrownBy(() -> service.summarize(999_999L))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
