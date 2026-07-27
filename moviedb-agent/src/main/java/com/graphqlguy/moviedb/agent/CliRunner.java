package com.graphqlguy.moviedb.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

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
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("moviedb-agent ready. Ask me about movies.");
            while (true) {
                System.out.print("> ");
                if (!in.hasNextLine()) break;
                String userInput = in.nextLine();
                if (userInput.isBlank()) continue;
                if ("quit".equalsIgnoreCase(userInput.trim())) break;

                String response = chatClient.prompt(userInput)
                    .call()
                    .content();
                System.out.println(response);
                System.out.println();
            }
        }
    }
}
