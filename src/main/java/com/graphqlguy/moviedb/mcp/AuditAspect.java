package com.graphqlguy.moviedb.mcp;

import tools.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

/**
 * Class 16: one structured audit line per tool invocation on the dedicated
 * logger {@code audit.mcp}, carrying user identity, tool, arguments, outcome,
 * and duration. Route that logger to its own appender in production, and redact
 * sensitive arguments before they reach the audit stream.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger("audit.mcp");
    private final ObjectMapper mapper;

    public AuditAspect(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Around("@annotation(org.springframework.ai.mcp.annotation.McpTool)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        String user = currentPrincipal();
        String tool = joinPoint.getSignature().toShortString();
        Instant start = Instant.now();

        Map<String, Object> entry = Map.of(
            "ts", start.toString(),
            "user", user,
            "tool", tool,
            "args", Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            log.info("{}", mapper.writeValueAsString(Map.of(
                "outcome", "ok",
                "durationMs", Instant.now().toEpochMilli() - start.toEpochMilli(),
                "audit", entry)));
            return result;
        } catch (Throwable t) {
            log.error("{}", mapper.writeValueAsString(Map.of(
                "outcome", "error",
                "exception", t.getClass().getName(),
                "message", String.valueOf(t.getMessage()),
                "audit", entry)));
            throw t;
        }
    }

    private String currentPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "anonymous" : auth.getName();
    }
}
