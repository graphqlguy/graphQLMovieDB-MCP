package com.graphqlguy.moviedb.mcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Class 15: /mcp is now an OAuth 2.1 resource server. The test config supplies a
// JwtDecoder that accepts a canned token, and the transport attaches it.
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(McpSecurityTestConfig.class)
class McpEndpointIntegrationTest {

    @LocalServerPort
    int port;

    McpSyncClient client;

    @BeforeEach
    void connect() {
        var transport = HttpClientStreamableHttpTransport
            .builder("http://localhost:" + port)
            .endpoint("/mcp")
            .httpRequestCustomizer((builder, method, uri, body, ctx) -> builder.header(
                "Authorization", "Bearer " + McpSecurityTestConfig.TEST_TOKEN))
            .build();
        client = McpClient.sync(transport).build();
        client.initialize();
    }

    @AfterEach
    void disconnect() {
        client.closeGracefully();
    }

    @Test
    void toolsListIncludesAllRegisteredTools() {
        McpSchema.ListToolsResult tools = client.listTools();

        assertThat(tools.tools())
            .extracting(McpSchema.Tool::name)
            .contains("recommendMoviesForMood", "summarizeMovieReviews",
                      "addToWatchlist", "ping");
    }

    @Test
    void recommendMoviesForMoodEndToEnd() {
        McpSchema.CallToolResult result = client.callTool(
            new McpSchema.CallToolRequest("recommendMoviesForMood",
                Map.of("input", Map.of("mood", "COMFORT", "excludeWatched", false))));

        assertThat(result.isError()).isNotEqualTo(true);
        assertThat(result.content()).isNotEmpty();
    }
}
