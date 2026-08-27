package com.graphqlguy.moviedb.recommendation;

import com.graphqlguy.moviedb.movie.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RecommendationServiceTest {

    @Autowired
    private RecommendationService service;

    @Test
    void differentMoodsReturnDifferentMovies() {
        List<Movie> horror = service.recommendForMood(Mood.HORROR, false, null);
        List<Movie> comedy = service.recommendForMood(Mood.COMEDY, false, null);

        assertThat(horror).isNotEmpty();
        assertThat(comedy).isNotEmpty();
        assertThat(horror).isNotEqualTo(comedy);
    }

    @Test
    void horrorOnlyReturnsMoodAppropriateGenres() {
        List<Movie> horror = service.recommendForMood(Mood.HORROR, false, null);

        assertThat(horror)
            .extracting(Movie::getGenre)
            .allMatch(MoodProfile.forMood(Mood.HORROR).genres()::contains);
    }
}
