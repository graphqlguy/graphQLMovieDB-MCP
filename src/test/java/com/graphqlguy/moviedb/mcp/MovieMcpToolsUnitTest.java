package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.mcp.MovieMcpTools.MovieSummary;
import com.graphqlguy.moviedb.mcp.MovieMcpTools.RecommendInput;
import com.graphqlguy.moviedb.recommendation.Mood;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.graphql.ExecutionGraphQlRequest;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.ResponseField;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MovieMcpToolsUnitTest {

    @Test
    void recommendMoviesForMood_returnsCuratedMovies() {
        ExecutionGraphQlService graphql = Mockito.mock(ExecutionGraphQlService.class);
        ExecutionGraphQlResponse response = Mockito.mock(ExecutionGraphQlResponse.class);
        ResponseField field = Mockito.mock(ResponseField.class);

        // Raw Maps are exactly what the GraphQL engine hands back for a list of
        // objects; note genre arriving as the serialized enum name "ROMANCE".
        when(field.getValue()).thenReturn(List.of(
            Map.of("id", "1", "title", "Amelie", "releaseYear", 2001,
                   "genre", "ROMANCE", "rating", 8.3),
            Map.of("id", "2", "title", "Paddington 2", "releaseYear", 2017,
                   "genre", "COMEDY", "rating", 8.0)
        ));
        when(response.field("recommendMoviesForMood")).thenReturn(field);
        when(graphql.execute(any(ExecutionGraphQlRequest.class))).thenReturn(Mono.just(response));

        MovieMcpTools tools = new MovieMcpTools(graphql, new ObjectMapper());

        List<MovieSummary> result = tools.recommendMoviesForMood(
            new RecommendInput(Mood.COMFORT, false));

        assertThat(result)
            .extracting(MovieSummary::title)
            .containsExactly("Amelie", "Paddington 2");
    }
}
