package com.graphqlguy.moviedb.mcp;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class HelloMcpTool {

    @McpTool(
        name = "ping",
        description = "Smoke-test tool. Returns a greeting that echoes the supplied name. " +
                      "Used during Class 6 to verify the MCP server wiring before real tools are added in Class 7.")
    public String ping(
        @McpToolParam(
            description = "Name to greet. Any non-empty string.",
            required = true
        ) String name) {
        return "hello, " + name;
    }
}
