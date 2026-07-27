package com.graphqlguy.moviedb.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class 18: a ChatClient wired with the MCP tool callbacks the client starter
 * auto-discovered. The defaultSystem prompt aligns the agent's behavior with the
 * server contract - notably how to handle the null summary from
 * summarizeMovieReviews so the model reports "not enough reviews yet" rather than
 * hallucinating one.
 */
@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider mcpTools) {
        return builder
            .defaultSystem("""
                You are a helpful movie recommender. The user is asking for recommendations
                or watchlist actions. You have three tools available, all backed by the
                user's local movie database. Prefer calling a tool over speculating from
                memory; if the user gives you a movie ID and asks about it, look it up.
                If a tool returns null for a summary, tell the user "not enough reviews
                yet" rather than guessing.
                """)
            .defaultToolCallbacks(mcpTools)
            .build();
    }
}
