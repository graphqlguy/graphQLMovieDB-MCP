package com.graphqlguy.moviedb.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * Class 15: a second security filter chain, scoped by {@code securityMatcher}
 * to the MCP paths and ordered ahead of the prerequisite tutorial's HMAC chain.
 * It makes /mcp an OAuth 2.1 resource server: bearer JWTs validated against the
 * configured issuer's JWK Set, while the discovery endpoints stay public. The
 * two chains never contend because this one matches only /mcp and the
 * well-known paths; everything else falls through to the default chain.
 */
// Class 19: gated off in stdio mode alongside SecurityConfig.
@Profile("!stdio")
@Configuration
public class McpSecurityConfig {

    @Value("${moviedb.mcp.resource}")
    private String resource;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Bean
    @Order(1)
    SecurityFilterChain mcpFilterChain(HttpSecurity http) throws Exception {
        // RFC 9728 section 5.1: a 401 from a protected resource should carry a
        // WWW-Authenticate header with a resource_metadata parameter, which
        // tells a client without a token where the metadata document is.
        // Spring Security 7's default BearerTokenAuthenticationEntryPoint adds
        // that parameter itself, built from the address of the request, so the
        // chain keeps the default.
        http
            // /mcp is a stateless bearer-token API, so both of these matter.
            // CsrfFilter would otherwise reject every POST without a token,
            // and an MCP streamable-HTTP client's very first request is a POST
            // `initialize`: the client would get a 403 with no
            // WWW-Authenticate header and no way to discover what it needs.
            // The default chain disables both for the same reason.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .securityMatcher("/mcp", "/mcp/**",
                "/.well-known/oauth-protected-resource",
                "/.well-known/oauth-protected-resource/mcp")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/.well-known/oauth-protected-resource",
                    "/.well-known/oauth-protected-resource/mcp").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> {})
                // Spring Security serves the RFC 9728 document itself, from a
                // filter that runs ahead of the DispatcherServlet. Describing
                // the resource here is the only way to change that document. A
                // @RestController mapped to the same paths is bypassed, because
                // the filter answers those requests first.
                .protectedResourceMetadata(metadata -> metadata
                    .protectedResourceMetadataCustomizer(builder -> builder
                        .resource(resource)
                        .authorizationServer(issuer)
                        .scopes(scopes -> scopes.addAll(List.of(
                            "movies:read", "watchlist:read",
                            "watchlist:write", "reviews:read")))
                        .claim("resource_documentation",
                            "https://graphqlguy.com/docs/tutorial-graphql-mcp"))));
        return http.build();
    }
}
