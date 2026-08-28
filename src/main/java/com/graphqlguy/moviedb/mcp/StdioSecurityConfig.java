package com.graphqlguy.moviedb.mcp;

import com.graphqlguy.moviedb.user.AppUser;
import com.graphqlguy.moviedb.user.Role;
import com.graphqlguy.moviedb.user.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Profile("stdio")
@Configuration
public class StdioSecurityConfig {

    static final String LOCAL_USERNAME = "desktop-user";

    // A stdio server is one subprocess belonging to one OS user. MODE_GLOBAL
    // shares a single security context across all threads, so the scope checks
    // from Class 15 and the user-scoped watchlist logic keep working against
    // one fixed local principal.
    @PostConstruct
    void seedLocalIdentity() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        var authentication = new UsernamePasswordAuthenticationToken(
                LOCAL_USERNAME, "n/a",
                AuthorityUtils.createAuthorityList(
                        "SCOPE_movies:read", "SCOPE_reviews:read",
                        "SCOPE_watchlist:read", "SCOPE_watchlist:write"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Authenticating as a name the database has never heard of gets you past
    // every scope check and then fails at the first user-scoped operation:
    // WatchlistService resolves the principal to an AppUser row and refuses
    // when there is none. A fixed identity is still an identity, so it needs
    // an account like any other. @Order(1) runs this after DataInitializer's
    // unordered runner, which is what creates the schema's other users.
    @Bean
    @Order(1)
    CommandLineRunner ensureLocalAccount(UserRepository users, PasswordEncoder passwordEncoder) {
        return args -> {
            if (users.findByUsername(LOCAL_USERNAME).isEmpty()) {
                users.save(AppUser.builder()
                        .username(LOCAL_USERNAME)
                        .email("desktop-user@localhost")
                        .password(passwordEncoder.encode("n/a"))
                        .role(Role.USER)
                        .build());
            }
        };
    }
}
