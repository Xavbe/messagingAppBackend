package messagingApp.controller.conversation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationResponse(
        UUID conversationId,
        String conversationName,
        String message,
        LocalDateTime updatedAt
) {
    public static ConversationResponse from(messagingApp.infrastructure.Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getName(),
                "",
                conversation.getLastMessageAt()
        );
    }
}