package messagingApp.controller.conversation;

import java.util.List;

public record CreateConversationRequest(String conversationName, List<String> usernames) {
}
