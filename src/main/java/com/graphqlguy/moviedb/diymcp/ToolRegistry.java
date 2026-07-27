package com.graphqlguy.moviedb.diymcp;

import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Class 13: the tool registry we hand-build for the DIY MCP path. Spring AI's
 * starter has the same concept behind its auto-configuration; here it is a
 * plain map from tool name to metadata plus an execution callback.
 */
@Component
public class ToolRegistry {

    public record Tool(String name, String description, String inputSchema, Function<JsonNode, Object> handler) {}

    private final Map<String, Tool> tools = new LinkedHashMap<>();

    public void register(Tool tool) {
        tools.put(tool.name(), tool);
    }

    public Map<String, Tool> all() {
        return tools;
    }

    public Tool get(String name) {
        return tools.get(name);
    }
}
