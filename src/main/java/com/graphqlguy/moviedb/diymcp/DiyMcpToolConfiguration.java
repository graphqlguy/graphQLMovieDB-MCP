package com.graphqlguy.moviedb.diymcp;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.graphqlguy.moviedb.mcp.MovieOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.ResponseError;
import org.springframework.graphql.support.DefaultExecutionGraphQlRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class 13: hand-registers the three movie operations into the DIY
 * {@link ToolRegistry} on startup. Each tool carries a hand-written JSON Schema
 * for its inputs (the Track B starter generates this from the method signature)
 * and a handler that bridges into {@link ExecutionGraphQlService}, exactly what
 * the starter's MethodToolCallback does at runtime, spelled out.
 */
@Configuration
public class DiyMcpToolConfiguration {

    @Autowired ToolRegistry registry;
    @Autowired ExecutionGraphQlService graphql;
    @Autowired ObjectMapper mapper;

    @PostConstruct
    void registerTools() {
        registry.register(new ToolRegistry.Tool(
            "recommendMoviesForMood",
            "Recommend movies that fit a given mood.",
            """
            {
              "type": "object",
              "properties": {
                "mood": {"type": "string", "enum": ["COMFORT","ADVENTURE","ROMANCE","HORROR","THOUGHTFUL","COMEDY"]},
                "excludeWatched": {"type": "boolean"}
              },
              "required": ["mood"]
            }
            """,
            args -> executeOperation("RecommendMoviesForMood",
                MovieOperations.RECOMMEND_MOVIES_FOR_MOOD,
                jsonToVariables(args))));

        registry.register(new ToolRegistry.Tool(
            "summarizeMovieReviews",
            "Summarize user reviews for a specific movie.",
            """
            {
              "type": "object",
              "properties": {
                "movieId": {"type": "string"}
              },
              "required": ["movieId"]
            }
            """,
            args -> executeOperation("SummarizeMovieReviews",
                MovieOperations.SUMMARIZE_MOVIE_REVIEWS,
                jsonToVariables(args))));

        registry.register(new ToolRegistry.Tool(
            "addToWatchlist",
            "Add a movie or TV show to the signed-in user's watch list. "
                + "Provide exactly one of subject.movieId or subject.tvShowId.",
            """
            {
              "type": "object",
              "properties": {
                "subject": {
                  "type": "object",
                  "properties": {
                    "movieId": {"type": "string"},
                    "tvShowId": {"type": "string"}
                  }
                },
                "status": {"type": "string", "enum": ["WANT_TO_WATCH","WATCHED"]}
              },
              "required": ["subject"]
            }
            """,
            args -> executeOperation("AddToWatchlist",
                MovieOperations.ADD_TO_WATCHLIST,
                jsonToVariables(args))));
    }

    // Bug fix: graphql.execute(...) starts graphql-java's own
    // CompletableFuture-based engine directly; it never subscribes through a
    // Reactor pipeline tied to this call's thread, so the JWT filter's
    // thread-local SecurityContext never reaches field resolution on its own.
    // Spring for GraphQL restores security state per field from the
    // ExecutionInput's GraphQLContext, keyed by SecurityContext.class.getName()
    // (the same key SecurityContextThreadLocalAccessor reads). Seeding that key
    // here is what carries this thread's Authentication down to
    // WatchlistController's Principal argument and its
    // @PreAuthorize("isAuthenticated()"); an unauthenticated caller has no
    // Authentication to seed, so it is still refused there.
    private Object executeOperation(String name, String document, Map<String, Object> variables) {
        var request = new DefaultExecutionGraphQlRequest(
            document, name, variables, Map.of(),
            UUID.randomUUID().toString(), null);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            request.configureExecutionInput((executionInput, builder) -> {
                executionInput.getGraphQLContext().put(SecurityContext.class.getName(), securityContext);
                return executionInput;
            });
        }
        var response = graphql.execute(request).block();
        // A failed execution (validation error, resolver exception) returns
        // null data plus entries in getErrors(); swallowing them would answer
        // the agent with isError: false and a useless "null". Throw instead,
        // so the catch in toolsCall turns the failure into isError: true.
        if (!response.isValid() || !response.getErrors().isEmpty()) {
            throw new IllegalStateException(response.getErrors().stream()
                .map(ResponseError::getMessage)
                .collect(Collectors.joining("; ")));
        }
        return response.getData();
    }

    private Map<String, Object> jsonToVariables(JsonNode args) {
        return mapper.convertValue(args, new TypeReference<Map<String, Object>>() {});
    }
}
