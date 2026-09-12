package com.graphqlguy.moviedb.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * Class 18: the full loop in a single CLI. chatClient.prompt(...).call().content()
 * lets Spring AI drive the tool-call protocol - when the model decides to call a
 * tool, the framework dispatches it to the MCP client, awaits the result, and
 * feeds it back into the model before .content() returns.
 */
@Component
public class CliRunner implements CommandLineRunner {

    private final ChatClient chatClient;

    public CliRunner(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run(String... args) {
        // One conversation per run. The memory advisor stores the messages under
        // this id and adds them to every prompt that carries it.
        String conversationId = UUID.randomUUID().toString();
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("moviedb-agent ready. Ask me about movies.");
            while (true) {
                System.out.print("> ");
                if (!in.hasNextLine()) break;
                String userInput = in.nextLine();
                if (userInput.isBlank()) continue;
                if ("quit".equalsIgnoreCase(userInput.trim())) break;

                // The tool context becomes the MCP request's _meta, and a progressToken
                // there asks the server for progress notifications.
                String response = chatClient.prompt(userInput)
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .toolContext(Map.of("progressToken", UUID.randomUUID().toString()))
                    .call()
                    .content();
                System.out.println(response);
                System.out.println();
            }
        }
    }
}
