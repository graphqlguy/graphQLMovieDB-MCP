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
 * string into a principal carrying every scope the tools require. The subject
 * claim is the token text itself, so a test can authenticate as any seeded
 * {@code AppUser} (for example "user" or "mara") just by sending that
 * username as the bearer token; {@link #TEST_TOKEN} keeps working for tests
 * that only need an authenticated caller and never touch a user-owned record.
 */
@TestConfiguration
public class McpSecurityTestConfig {

    static final String TEST_TOKEN = "test-user";
    static final String ALL_SCOPES = "movies:read watchlist:read watchlist:write reviews:read";

    @Bean
    JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token)
            .header("alg", "none")
            .subject(token)
            .claim("scope", ALL_SCOPES)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(300))
            .build();
    }
}
