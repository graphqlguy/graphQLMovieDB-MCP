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

    private Object executeOperation(String name, String document, Map<String, Object> variables) {
        var request = new DefaultExecutionGraphQlRequest(
            document, name, variables, Map.of(),
            UUID.randomUUID().toString(), null);
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
