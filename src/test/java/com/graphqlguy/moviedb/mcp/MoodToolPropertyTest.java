package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.mcp.MovieMcpTools.RecommendInput;
import com.graphqlguy.moviedb.recommendation.Mood;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;

class MoodToolPropertyTest {

    @ParameterizedTest
    @EnumSource(Mood.class)
    void recommendMoviesForMood_acceptsEveryMoodValue(Mood mood) {
        assertThatCode(() -> {
            MovieMcpTools tools = withMockedGraphQl();
            tools.recommendMoviesForMood(new RecommendInput(mood, false));
        }).doesNotThrowAnyException();
    }

    private MovieMcpTools withMockedGraphQl() {
        return new MovieMcpTools(MockGraphQlServiceFactory.empty());
    }
}
