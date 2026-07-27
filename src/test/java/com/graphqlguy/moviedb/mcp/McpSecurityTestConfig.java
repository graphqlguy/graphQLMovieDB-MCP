package com.graphqlguy.moviedb.mcp;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;

/**
 * Class 15 test support. The production /mcp chain validates JWTs against an
 * external issuer's JWK Set, which no test can reach. This test-only
 * {@link JwtDecoder} bean replaces the network-backed decoder (Boot's own bean
 * is {@code @ConditionalOnMissingBean}, so it backs off) and decodes any token
 * string into a principal carrying every scope the tools require. The boot MCP
 * tests attach {@link #TEST_TOKEN} as a bearer token; the decoder accepts it.
 */
@TestConfiguration
public class McpSecurityTestConfig {

    static final String TEST_TOKEN = "test-token";
    static final String ALL_SCOPES = "movies:read watchlist:read watchlist:write reviews:read";

    @Bean
    JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token)
            .header("alg", "none")
            .subject("test-user")
            .claim("scope", ALL_SCOPES)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(300))
            .build();
    }
}
