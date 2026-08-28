package com.graphqlguy.moviedb.mcp;

import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.ResponseError;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class 9: central mapping from GraphQL execution outcomes onto MCP
 * CallToolResult. Three cases: total failure (errors, data null) becomes a
 * textual isError result; partial success (errors and data) and full success
 * both become a data-plus-errors payload delivered as structuredContent plus
 * a serialized text block for older clients, with isError marking partiality.
 */
@Component
public class ToolResults {

    private final ObjectMapper mapper;

    public ToolResults(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public CallToolResult toCallResult(ExecutionGraphQlResponse response, String fieldName) {
        List<ResponseError> errors = response.getErrors();
        Object data = response.field(fieldName).getValue();

        if (!errors.isEmpty() && data == null) {
            return error("GraphQL operation failed: " + summarizeErrors(errors));
        }

        // HashMap, not Map.of: data can legitimately be null without errors
        // (a nullable field, such as movie(id) for an unknown id, resolves
        // to null on purpose).
        Map<String, Object> payload = new HashMap<>();
        payload.put("data", data);
        payload.put("errors", errors.stream().map(this::errorAsMap).toList());

        try {
            return CallToolResult.builder()
                .structuredContent(payload)
                .addTextContent(mapper.writeValueAsString(payload))
                .isError(!errors.isEmpty())
                .build();
        } catch (Exception e) {
            return error("Result serialization failed: " + e.getMessage());
        }
    }

    public CallToolResult error(String message) {
        return CallToolResult.builder()
            .addTextContent(message)
            .isError(true)
            .build();
    }

    // Promote the classification to a first-class field so the agent can pick
    // its next move (NOT_FOUND: search first; BAD_REQUEST: fix the input;
    // UNAUTHORIZED: ask the user to sign in; FORBIDDEN: stop and say so).
    private Map<String, Object> errorAsMap(ResponseError err) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", err.getMessage());
        result.put("path", err.getParsedPath());

        Map<String, Object> extensions = new HashMap<>(
            err.getExtensions() == null ? Map.of() : err.getExtensions());
        Object classification = extensions.remove("classification");
        if (classification != null) {
            result.put("classification", classification);
        }
        if (!extensions.isEmpty()) {
            result.put("extensions", extensions);
        }
        return result;
    }

    private String summarizeErrors(List<ResponseError> errors) {
        return errors.stream()
            .map(ResponseError::getMessage)
            .collect(Collectors.joining("; "));
    }
}
