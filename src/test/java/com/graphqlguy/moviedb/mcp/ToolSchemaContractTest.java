package com.graphqlguy.moviedb.mcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Class 15: authenticate against the resource-server-protected /mcp endpoint.
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(McpSecurityTestConfig.class)
class ToolSchemaContractTest {

    @LocalServerPort int port;
    @Autowired ObjectMapper mapper;

    @Test
    void toolSchemasMatchSnapshot() throws Exception {
        var transport = HttpClientStreamableHttpTransport
            .builder("http://localhost:" + port)
            .endpoint("/mcp")
            .httpRequestCustomizer((builder, method, uri, body, ctx) -> builder.header(
                "Authorization", "Bearer " + McpSecurityTestConfig.TEST_TOKEN))
            .build();
        McpSyncClient client = McpClient.sync(transport).build();
        client.initialize();

        McpSchema.ListToolsResult tools = client.listTools();
        client.closeGracefully();

        String actual = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(tools.tools().stream()
                .sorted(Comparator.comparing(McpSchema.Tool::name))
                .toList());

        String expected = new ClassPathResource("contracts/tools-list.json")
            .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actual)
            .as("If this fails, the tool schema changed. Either revert the change, or update the contract file.")
            .isEqualTo(expected);
    }
}
