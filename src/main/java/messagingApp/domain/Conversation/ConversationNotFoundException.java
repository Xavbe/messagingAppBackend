package messagingApp.domain.Conversation;

import java.util.UUID;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(UUID conversationId) {
        super("Conversation with id " + conversationId + " not found");
    }
}