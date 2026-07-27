package com.graphqlguy.moviedb.mcp;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Class 17: per-tool counters and timers for capacity planning. Emits
 * {@code mcp.tool.calls} (graph as call rate) and {@code mcp.tool.duration}
 * (graph as latency percentiles) tagged by tool name and outcome, into whichever
 * Micrometer registry the stack scrapes or receives. Sits alongside the Class 16
 * audit aspect on the same {@code @McpTool} pointcut; both advices run per call.
 */
@Aspect
@Component
public class ToolMetricsAspect {

    private final MeterRegistry meters;

    public ToolMetricsAspect(MeterRegistry meters) {
        this.meters = meters;
    }

    @Around("@annotation(org.springframework.ai.mcp.annotation.McpTool)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        String toolName = joinPoint.getSignature().getName();
        Timer.Sample sample = Timer.start(meters);
        String outcome = "success";

        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            outcome = "error";
            throw t;
        } finally {
            sample.stop(meters.timer("mcp.tool.duration",
                "tool", toolName,
                "outcome", outcome));
            meters.counter("mcp.tool.calls",
                "tool", toolName,
                "outcome", outcome).increment();
        }
    }
}
