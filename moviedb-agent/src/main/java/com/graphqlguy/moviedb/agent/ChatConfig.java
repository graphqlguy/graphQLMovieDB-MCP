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
                or watchlist actions. Your tools are all backed by the user's local movie
                database; the tool list you were given is the authoritative one, so work
                from it instead of from any assumption about what is available. Prefer
                calling a tool over speculating from memory; if the user gives you a movie
                ID and asks about it, look it up. If a tool returns null for a summary,
                tell the user "not enough reviews yet" instead of guessing. If a tool
                stages an action and returns a confirmation token, do not confirm it on
                your own: report what will happen and wait for the user to ask for it.
                """)
            .defaultToolCallbacks(mcpTools)
            .build();
    }
}
