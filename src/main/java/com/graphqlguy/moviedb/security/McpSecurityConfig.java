package com.graphqlguy.moviedb.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Class 15: a second security filter chain, scoped by {@code securityMatcher}
 * to the MCP paths and ordered ahead of the prerequisite tutorial's HMAC chain.
 * It makes /mcp an OAuth 2.1 resource server: bearer JWTs validated against the
 * configured issuer's JWK Set, while the discovery endpoints stay public. The
 * two chains never contend because this one matches only /mcp and the
 * well-known paths; everything else falls through to the default chain.
 */
@Configuration
public class McpSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain mcpFilterChain(HttpSecurity http) throws Exception {
        // RFC 9728 section 5.1: a 401 from a protected resource should carry a
        // WWW-Authenticate header with a resource_metadata parameter so a
        // tokenless client learns where the discovery document lives. Spring's
        // default BearerTokenAuthenticationEntryPoint emits a bare challenge, so
        // we replace it.
        AuthenticationEntryPoint entryPoint = (request, response, authException) -> {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer resource_metadata=" +
                "\"https://api.example.com/.well-known/oauth-protected-resource/mcp\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        };
        http
            .securityMatcher("/mcp", "/mcp/**",
                "/.well-known/oauth-protected-resource",
                "/.well-known/oauth-protected-resource/mcp")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/.well-known/oauth-protected-resource",
                    "/.well-known/oauth-protected-resource/mcp").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> {})
                .authenticationEntryPoint(entryPoint));
        return http.build();
    }
}
