package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.movie.MovieRepository;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Class 15: /mcp is now an OAuth 2.1 resource server. The test config supplies a
// JwtDecoder that accepts a canned token, and the transport attaches it.
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(McpSecurityTestConfig.class)
class McpEndpointIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    MovieRepository movieRepository;

    McpSyncClient client;

    @Autowired
    com.graphqlguy.moviedb.watchlist.WatchlistItemRepository watchlistItemRepository;

    @Autowired
    com.graphqlguy.moviedb.user.UserRepository userRepository;

    @BeforeEach
    void connect() {
        client = clientAs(McpSecurityTestConfig.TEST_TOKEN);
    }

    @AfterEach
    void disconnect() {
        client.closeGracefully();
    }

    // The bearer token IS the username under McpSecurityTestConfig's test
    // JwtDecoder, so a null token builds a transport that sends no
    // Authorization header at all: the unauthenticated case.
    private McpSyncClient clientAs(String usernameAsBearerToken) {
        var builder = HttpClientStreamableHttpTransport
            .builder("http://localhost:" + port)
            .endpoint("/mcp");
        if (usernameAsBearerToken != null) {
            builder.httpRequestCustomizer((requestBuilder, method, uri, body, ctx) ->
                requestBuilder.header("Authorization", "Bearer " + usernameAsBearerToken));
        }
        McpSyncClient syncClient = McpClient.sync(builder.build()).build();
        syncClient.initialize();
        return syncClient;
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

    // Regression test for the identity-loss bug: addToWatchlist runs its
    // mutation through an in-process ExecutionGraphQlService call, one layer
    // below the /mcp request the client just authenticated. "mara" is seeded
    // with no prior watchlist entries, so a fresh add can only fail on
    // identity, not on the duplicate-title rule exercised below.
    @Test
    void addToWatchlistEndToEndAsAuthenticatedUser() {
        Long movieId = movieRepository.findByTitleContainingIgnoreCase("Inception")
            .get(0).getId();
        McpSyncClient mara = clientAs("mara");
        try {
            McpSchema.CallToolResult result = mara.callTool(
                new McpSchema.CallToolRequest("addToWatchlist",
                    Map.of("subject", Map.of("movieId", movieId.toString()))));

            assertThat(result.isError()).isNotEqualTo(true);
            assertThat(result.content()).isNotEmpty();
        } finally {
            mara.closeGracefully();
        }
    }

    // The fix must not weaken security: a request with no bearer token at all
    // still has to be refused by the /mcp resource server chain before the
    // tool method, let alone the mutation, ever runs. McpSecurityConfig
    // requires authentication for every /mcp path, so an anonymous caller is
    // rejected during the MCP session handshake itself, before a tool call is
    // even possible.
    @Test
    void addToWatchlistRefusesUnauthenticatedCaller() {
        assertThatThrownBy(() -> clientAs(null))
            .hasMessageContaining("Client failed to initialize");
    }

    // "user" already has The Shawshank Redemption on their seeded watchlist;
    // adding it again must still surface the documented duplicate error
    // (InvalidInputException, classified BAD_REQUEST by GlobalExceptionHandler)
    // instead of the fix accidentally papering over it. This response has no
    // data, so ToolResults.toCallResult takes its all-error branch and
    // reports only the summarized message, not the classification: the
    // message itself is the contract addToWatchlist's own description
    // promises callers ("... fails with an \"already in your watch list\"
    // error").
    @Test
    void addToWatchlistStillRejectsDuplicateAdd() {
        Long movieId = movieRepository.findByTitleContainingIgnoreCase("Shawshank")
            .get(0).getId();
        McpSyncClient user = clientAs("user");
        try {
            McpSchema.CallToolResult result = user.callTool(
                new McpSchema.CallToolRequest("addToWatchlist",
                    Map.of("subject", Map.of("movieId", movieId.toString()))));

            assertThat(result.isError()).isEqualTo(true);
            String text = ((McpSchema.TextContent) result.content().get(0)).text();
            assertThat(text).contains("already in your watch list");
        } finally {
            user.closeGracefully();
        }
    }

    // Regression test for the same identity-loss bug one layer deeper.
    // confirmAction executes the staged mutation through its own
    // ExecutionGraphQlService call in SafeMutationTools, which needs the
    // security context seeded exactly the way MovieMcpTools seeds it. Without
    // that seeding the removal is refused as Unauthorized and the whole
    // stage-and-confirm feature silently does nothing, which is worse than
    // failing loudly: the tool reports an error the user reads as a permission
    // problem on their own data.
    @Test
    void stageAndConfirmActuallyRemovesTheItem() {
        McpSyncClient user = clientAs("user");
        try {
            Long userId = userRepository.findByUsername("user").orElseThrow().getId();
            Long itemId = watchlistItemRepository.findWithContentByUserId(userId)
                .get(0).getId();

            McpSchema.CallToolResult staged = user.callTool(
                new McpSchema.CallToolRequest("stageRemoveFromWatchlist",
                    Map.of("itemId", itemId.toString())));
            assertThat(staged.isError()).isNotEqualTo(true);

            String token = ((McpSchema.TextContent) staged.content().get(0)).text()
                .replaceAll(".*\"confirmationToken\"\\s*:\\s*\"([^\"]+)\".*", "$1");

            McpSchema.CallToolResult confirmed = user.callTool(
                new McpSchema.CallToolRequest("confirmAction",
                    Map.of("confirmationToken", token)));

            String text = ((McpSchema.TextContent) confirmed.content().get(0)).text();
            assertThat(text).contains("\"status\":\"ok\"");
            assertThat(text).doesNotContain("Unauthorized");
            assertThat(watchlistItemRepository.findById(itemId)).isEmpty();
        } finally {
            user.closeGracefully();
        }
    }

}
