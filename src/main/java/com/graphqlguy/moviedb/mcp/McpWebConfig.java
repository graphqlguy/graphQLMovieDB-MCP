package com.graphqlguy.moviedb.mcp;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Class 16: registers the per-session rate limiter on the MCP paths and enables
 * the scheduler that drives {@link SafeMutationTools#sweepExpired()}.
 */
@Configuration
@EnableScheduling
public class McpWebConfig implements WebMvcConfigurer {

    private final SessionRateLimitInterceptor rateLimitInterceptor;

    public McpWebConfig(SessionRateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/mcp", "/mcp/**");
    }
}
