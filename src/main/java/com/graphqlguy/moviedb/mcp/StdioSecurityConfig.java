package com.graphqlguy.moviedb.mcp;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

@Profile("stdio")
@Configuration
public class StdioSecurityConfig {

    // A stdio server is one subprocess belonging to one OS user. MODE_GLOBAL
    // shares a single security context across all threads, so the scope checks
    // from Class 15 and the user-scoped watchlist logic keep working against
    // one fixed local principal.
    @PostConstruct
    void seedLocalIdentity() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        var authentication = new UsernamePasswordAuthenticationToken(
                "desktop-user", "n/a",
                AuthorityUtils.createAuthorityList(
                        "SCOPE_movies:read", "SCOPE_reviews:read",
                        "SCOPE_watchlist:read", "SCOPE_watchlist:write"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
