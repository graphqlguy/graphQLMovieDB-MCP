package com.graphqlguy.moviedb.recommendation;

import com.graphqlguy.moviedb.movie.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

    @Test
    void noTwoMoodsReturnTheSameResultList() {
        Map<Mood, List<Movie>> resultsByMood = new EnumMap<>(Mood.class);
        for (Mood mood : Mood.values()) {
            resultsByMood.put(mood, service.recommendForMood(mood, false, null));
        }

        Mood[] moods = Mood.values();
        for (int i = 0; i < moods.length; i++) {
            for (int j = i + 1; j < moods.length; j++) {
                Mood first = moods[i];
                Mood second = moods[j];
                assertThat(resultsByMood.get(first))
                    .as("%s and %s should not return identical result lists", first, second)
                    .isNotEqualTo(resultsByMood.get(second));
            }
        }
    }
}
