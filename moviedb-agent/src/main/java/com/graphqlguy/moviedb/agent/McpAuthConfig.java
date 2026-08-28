package com.graphqlguy.moviedb.agent;

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import org.springframework.ai.mcp.customizer.McpClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class 18: attaches a bearer token to every outgoing MCP request, for talking
 * to the OAuth-protected /mcp endpoint Class 15 built. No configuration
 * property sets an Authorization header on the transport, so the seam is a
 * customizer the transport auto-configuration applies to its builder.
 *
 * Conditional on the property, so an unprotected server needs no change: leave
 * moviedb.mcp.token unset and this bean never exists.
 */
@Configuration
public class McpAuthConfig {

    @Bean
    @ConditionalOnProperty("moviedb.mcp.token")
    McpClientCustomizer<HttpClientStreamableHttpTransport.Builder> bearerTokenCustomizer(
            @Value("${moviedb.mcp.token}") String token) {
        return (serverName, builder) -> builder.httpRequestCustomizer(
            (requestBuilder, method, uri, body, context) ->
                requestBuilder.header("Authorization", "Bearer " + token));
    }
}
