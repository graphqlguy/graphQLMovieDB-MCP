package com.graphqlguy.moviedb.exception;

import com.graphqlguy.moviedb.config.GraphQLConfig;
import com.graphqlguy.moviedb.movie.MovieController;
import com.graphqlguy.moviedb.movie.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

// An ID argument is a string in the schema and a Long in the controller. A value
// that is not a number fails when Spring binds it to the Long, before the
// controller runs. Agents send such values (a movie title where an id belongs), so
// the error has to say which argument was wrong instead of reporting an internal error.
@GraphQlTest(value = MovieController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.graphqlguy\\.moviedb\\.instrumentation\\..*"))
@Import({GraphQLConfig.class, GlobalExceptionHandler.class})
class ArgumentConversionErrorTest {

    @Autowired
    GraphQlTester graphQlTester;

    @MockitoBean
    MovieService movieService;

    @Test
    void movie_whenIdIsNotANumber_shouldReturnBadRequestNamingTheArgument() {
        graphQlTester.document("{ movie(id: \"Casablanca\") { title } }")
                .execute()
                .errors().satisfy(errors -> assertThat(errors)
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.getErrorType()).hasToString("BAD_REQUEST");
                            assertThat(error.getMessage()).isEqualTo("Invalid value 'Casablanca' for id");
                            assertThat(error.getExtensions()).containsEntry("field", "id");
                        }));
    }
}
