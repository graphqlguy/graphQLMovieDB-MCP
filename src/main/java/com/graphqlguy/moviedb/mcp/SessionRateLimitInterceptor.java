package com.graphqlguy.moviedb.mcp;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class 16: a token bucket per authenticated principal, refined by session id
 * when present. Keying starts from the verified principal (the JWT sub) instead
 * of the client-supplied Mcp-Session-Id, because keying on a rotatable header
 * alone would let a misbehaving client mint a fresh bucket per request.
 */
@Component
public class SessionRateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        String path = req.getRequestURI();
        if (!path.equals("/mcp") && !path.startsWith("/mcp/")) return true;

        var auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth != null ? auth.getName() : req.getRemoteAddr();
        String sessionId = req.getHeader("Mcp-Session-Id");
        String key = sessionId == null ? principal : principal + ":" + sessionId;

        Bucket bucket = buckets.computeIfAbsent(key, k ->
            Bucket.builder()
                .addLimit(Bandwidth.builder()
                    .capacity(60)
                    .refillGreedy(60, Duration.ofMinutes(1))
                    .build())
                .build());

        if (!bucket.tryConsume(1)) {
            res.setStatus(429);
            return false;
        }
        return true;
    }
}
