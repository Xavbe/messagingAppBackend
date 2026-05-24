package messagingApp.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    private UUID conversationId;
    private UUID messageId;
    private UUID senderId;
    private String content;
    private LocalDateTime timestamp;

    public UUID getConversationId() {
        return conversationId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
