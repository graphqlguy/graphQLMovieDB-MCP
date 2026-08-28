package com.graphqlguy.moviedb.diymcp;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Class 13: a second MCP endpoint built entirely by hand, no Spring AI starter.
 * A single @RestController accepts JSON-RPC 2.0 requests on /diy-mcp/message and
 * dispatches by method. The point is to make the starter's abstraction legible:
 * everything here is code the starter would otherwise write for you.
 */
@RestController
@RequestMapping("/diy-mcp")
public class DiyMcpController {

    private static final Set<String> ALLOWED_ORIGINS =
        Set.of("http://localhost:6274");   // MCP Inspector's web UI

    private final ObjectMapper mapper;
    private final ToolRegistry tools;

    public DiyMcpController(ObjectMapper mapper, ToolRegistry tools) {
        this.mapper = mapper;
        this.tools = tools;
    }

    @PostMapping("/message")
    public ResponseEntity<JsonNode> handle(
            @RequestHeader(value = "Origin", required = false) String origin,
            @RequestBody JsonNode request) {
        // Transport MUST: validate Origin to defend against DNS rebinding.
        // Browsers always attach Origin on cross-origin requests; non-browser
        // clients (curl, server-side proxies) do not send one, so null passes.
        if (origin != null && !ALLOWED_ORIGINS.contains(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!"2.0".equals(request.path("jsonrpc").asText())) {
            return ResponseEntity.ok(error(request, -32600, "Invalid Request: missing jsonrpc 2.0"));
        }
        String method = request.path("method").asText();
        JsonNode id = request.get("id");

        return switch (method) {
            case "initialize" -> ResponseEntity.ok(initialize(request, id));
            // Notifications do not carry an id and MUST get an empty 202 Accepted.
            case "notifications/initialized" -> ResponseEntity.accepted().build();
            case "tools/list" -> ResponseEntity.ok(toolsList(id));
            case "tools/call" -> ResponseEntity.ok(toolsCall(request, id));
            default -> ResponseEntity.ok(error(request, -32601, "Method not found: " + method));
        };
    }

    private ObjectNode initialize(JsonNode request, JsonNode id) {
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", id);

        ObjectNode result = response.putObject("result");
        result.put("protocolVersion", "2025-11-25");

        ObjectNode serverInfo = result.putObject("serverInfo");
        serverInfo.put("name", "diy-moviedb-mcp");
        serverInfo.put("version", "1.0.0");

        ObjectNode capabilities = result.putObject("capabilities");
        capabilities.putObject("tools");

        return response;
    }

    private ObjectNode toolsList(JsonNode id) {
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", id);

        ObjectNode result = response.putObject("result");
        var toolsArray = result.putArray("tools");

        for (ToolRegistry.Tool tool : tools.all().values()) {
            ObjectNode entry = toolsArray.addObject();
            entry.put("name", tool.name());
            entry.put("description", tool.description());
            try {
                entry.set("inputSchema", mapper.readTree(tool.inputSchema()));
            } catch (Exception e) {
                entry.put("inputSchema", "{}");
            }
        }
        return response;
    }

    private ObjectNode toolsCall(JsonNode request, JsonNode id) {
        JsonNode params = request.path("params");
        String name = params.path("name").asText();
        JsonNode arguments = params.path("arguments");

        ToolRegistry.Tool tool = tools.get(name);
        if (tool == null) {
            return error(request, -32602, "Unknown tool: " + name);
        }

        try {
            Object result = tool.handler().apply(arguments);
            return successResult(id, result);
        } catch (Exception e) {
            return errorResult(id, "Tool execution failed: " + e.getMessage());
        }
    }

    private ObjectNode successResult(JsonNode id, Object data) {
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", id);
        ObjectNode result = response.putObject("result");
        result.put("isError", false);

        var content = result.putArray("content");
        ObjectNode item = content.addObject();
        item.put("type", "text");
        try {
            item.put("text", mapper.writeValueAsString(data));
        } catch (Exception e) {
            item.put("text", String.valueOf(data));
        }
        return response;
    }

    private ObjectNode errorResult(JsonNode id, String message) {
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", id);
        ObjectNode result = response.putObject("result");
        result.put("isError", true);
        var content = result.putArray("content");
        var item = content.addObject();
        item.put("type", "text");
        item.put("text", message);
        return response;
    }

    private ObjectNode error(JsonNode request, int code, String message) {
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        if (request.has("id")) response.set("id", request.get("id"));
        ObjectNode err = response.putObject("error");
        err.put("code", code);
        err.put("message", message);
        return response;
    }
}
