package com.graphqlguy.moviedb.agent;

import io.modelcontextprotocol.spec.McpSchema.ProgressNotification;
import org.springframework.ai.mcp.annotation.McpProgress;
import org.springframework.stereotype.Component;

/**
 * Class 18: handles server-initiated progress notifications (Class 10 wired these
 * on summarizeMovieReviews). The clients = "moviedb" attribute matches the server
 * connection name in application.yaml so this handler only fires for that server,
 * not every connected one.
 */
@Component
public class AgentNotifications {

    @McpProgress(clients = "moviedb")
    public void onProgress(ProgressNotification notification) {
        System.out.printf("[%.0f%%] %s%n",
            notification.progress() * 100.0,
            notification.message());
    }
}
