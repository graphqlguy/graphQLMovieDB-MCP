package com.graphqlguy.moviedb.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Class 15: the protected-resource-metadata endpoint RFC 9728 and the MCP spec
 * require. RFC 9728 inserts the well-known segment before the resource path, so
 * a conformant client requests /.well-known/oauth-protected-resource/mcp; we map
 * both that and the bare path for clients that treat the whole host as the
 * resource.
 */
@RestController
public class ProtectedResourceMetadataController {

    @Value("${moviedb.mcp.resource}")
    private String resource;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @GetMapping({
        "/.well-known/oauth-protected-resource",
        "/.well-known/oauth-protected-resource/mcp"
    })
    public Map<String, Object> metadata() {
        return Map.of(
            "resource", resource,
            "authorization_servers", List.of(issuer),
            "scopes_supported", List.of(
                "movies:read",
                "watchlist:read",
                "watchlist:write",
                "reviews:read"
            ),
            "bearer_methods_supported", List.of("header"),
            "resource_documentation", "https://graphqlguy.com/docs/tutorial-graphql-mcp"
        );
    }
}
