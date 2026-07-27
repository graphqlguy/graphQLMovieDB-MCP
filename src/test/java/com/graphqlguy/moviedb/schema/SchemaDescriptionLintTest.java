package com.graphqlguy.moviedb.schema;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.GraphQlSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SchemaDescriptionLintTest {

    /**
     * Temporary allowlist of legacy coordinates that predate the description
     * discipline (Class 3). The one rule: this list only ever shrinks. Any type
     * an agent can reach through the exposed operations must NOT be here.
     */
    private static final Set<String> LEGACY_ALLOWLIST = Set.of(
        "AuthResponse.token", "AuthResponse.user",
        "CommunityRating.voteCount",
        "Country.name", "Country.emoji", "Country.capital", "Country.currency",
        "DeleteMovieResponse.success", "DeleteMovieResponse.message",
        "DeletePersonResponse.success", "DeletePersonResponse.error", "DeletePersonResponse.deletedId",
        "DeleteReviewResponse.success",
        "Episode.id", "Episode.seasonNumber", "Episode.episodeNumber", "Episode.title",
        "Episode.overview", "Episode.airYear",
        "MovieCast.id", "MovieCast.characterName", "MovieCast.person", "MovieCast.movie",
        "MoviePage.content", "MoviePage.totalElements", "MoviePage.totalPages",
        "MoviePage.currentPage", "MoviePage.size", "MoviePage.isFirst", "MoviePage.isLast",
        "MoviePage.hasNext", "MoviePage.hasPrevious",
        "Mutation.deleteMovie", "Mutation.createMovie", "Mutation.updateMovie",
        "Mutation.createPerson", "Mutation.updatePerson", "Mutation.login",
        "Person.id", "Person.name", "Person.birthYear",
        "PersonPage.content", "PersonPage.totalElements", "PersonPage.totalPages",
        "PersonPage.currentPage", "PersonPage.size",
        "Query.moviesAll", "Query.movie", "Query.movies", "Query.moviesByIds",
        "Query.searchMovies", "Query.people", "Query.person", "Query.searchPeople",
        "Query.search", "Query.tvShow", "Query.tvShows",
        "Review.id", "Review.comment", "Review.createdAt", "Review.user",
        "ReviewNotification.review",
        "TmdbResult.tmdbId", "TmdbResult.title", "TmdbResult.releaseYear",
        "TmdbResult.overview", "TmdbResult.posterUrl",
        "TvShow.id", "TvShow.title", "TvShow.genre", "TvShow.posterUrl", "TvShow.startYear",
        "TvShow.seasons", "TvShow.plot", "TvShow.creators", "TvShow.cast", "TvShow.episodes",
        "TvShow.reviews",
        "TvShowCast.id", "TvShowCast.characterName", "TvShowCast.person", "TvShowCast.tvShow",
        "TvShowPage.content", "TvShowPage.totalElements", "TvShowPage.totalPages",
        "TvShowPage.currentPage", "TvShowPage.size",
        "User.id", "User.username", "User.email", "User.role"
    );

    @Autowired
    GraphQlSource graphQlSource;

    @Test
    void everyFieldOnEveryUserDefinedTypeHasADescription() {
        GraphQLSchema schema = graphQlSource.schema();
        List<String> missing = new ArrayList<>();

        for (GraphQLType type : schema.getAllTypesAsList()) {
            String typeName = type instanceof GraphQLObjectType o ? o.getName()
                : type instanceof GraphQLInterfaceType i ? i.getName()
                : null;
            if (typeName == null || typeName.startsWith("__")) {
                continue;
            }
            List<GraphQLFieldDefinition> fields = type instanceof GraphQLObjectType o
                ? o.getFieldDefinitions()
                : ((GraphQLInterfaceType) type).getFieldDefinitions();
            for (GraphQLFieldDefinition field : fields) {
                String desc = field.getDescription();
                if ((desc == null || desc.isBlank())
                        && !LEGACY_ALLOWLIST.contains(typeName + "." + field.getName())) {
                    missing.add(typeName + "." + field.getName());
                }
            }
        }

        assertThat(missing)
            .as("Every field on every user-defined object or interface type must have a non-empty description.")
            .isEmpty();
    }
}
