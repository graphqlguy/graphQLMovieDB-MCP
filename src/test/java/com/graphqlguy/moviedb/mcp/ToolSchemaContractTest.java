package com.graphqlguy.moviedb.mcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ToolSchemaContractTest {

    @LocalServerPort int port;
    @Autowired ObjectMapper mapper;

    @Test
    void toolSchemasMatchSnapshot() throws Exception {
        var transport = HttpClientStreamableHttpTransport
            .builder("http://localhost:" + port)
            .endpoint("/mcp")
            .build();
        McpSyncClient client = McpClient.sync(transport).build();
        client.initialize();

        McpSchema.ListToolsResult tools = client.listTools();
        client.closeGracefully();

        String actual = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(tools.tools());

        String expected = new ClassPathResource("contracts/tools-list.json")
            .getContentAsString(StandardCharsets.UTF_8);

        assertThat(actual)
            .as("If this fails, the tool schema changed. Either revert the change, or update the contract file.")
            .isEqualTo(expected);
    }
}
