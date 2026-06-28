package messagingApp.domain.message;

import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import messagingApp.infrastructure.MessageEntity.TextMessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    public List<MessageEntity> getMessage (UUID conversationId, UUID messageBeforeUUID, int limit) {
        return messageRepository.findMessagesBefore(conversationId, messageBeforeUUID, PageRequest.of(0, limit));
    }

    public MessageEntity sendMessage(
            UUID conversationId,
            long senderId,
            String content) {

        MessageEntity message = new TextMessageEntity();

        message.setId(UUID.randomUUID());
        message.setConversation(conversationService.getConversationById(conversationId));
        message.setSender(userService.findUserbyId(senderId));
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }
}
