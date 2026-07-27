package com.graphqlguy.moviedb.mcp;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Class 17: token metering for LLM-backed tools. An LLM call from a tool records
 * its prompt and completion token counts here; the meters roll up to per-tool,
 * per-model token totals that translate directly to cost at the provider's rate.
 * For sampling (client-side model), only the prompt length is predictable on the
 * server; completion tokens are billed to the user and counted client-side.
 */
@Component
public class CostMetrics {

    private final MeterRegistry meters;

    public CostMetrics(MeterRegistry meters) {
        this.meters = meters;
    }

    public void recordLlmCall(String tool, String model, long promptTokens, long completionTokens) {
        meters.counter("mcp.llm.tokens",
            "tool", tool,
            "model", model,
            "kind", "prompt").increment(promptTokens);
        meters.counter("mcp.llm.tokens",
            "tool", tool,
            "model", model,
            "kind", "completion").increment(completionTokens);
        meters.counter("mcp.llm.calls",
            "tool", tool,
            "model", model).increment();
    }
}
