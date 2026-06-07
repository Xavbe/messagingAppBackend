package messagingApp.controller.websocket;

import java.util.UUID;

public record SendMessageRequest(
        UUID conversationId,
        String content
) {}
