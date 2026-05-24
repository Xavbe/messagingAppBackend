package messagingApp.controller.message;

import java.util.List;

public record MessagesResponse(List<MessageResponse> messages, String conversationDone, String lastMessageId) {
}
