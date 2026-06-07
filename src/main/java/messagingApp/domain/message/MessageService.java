package messagingApp.domain.message;

import messagingApp.infrastructure.MessageEntity.MessageEntity;
import messagingApp.infrastructure.MessageEntity.TextMessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<MessageEntity> getMessage (UUID conversationId, UUID messageBeforeUUID, int limit) {
        return messageRepository.findMessagesBefore(conversationId, messageBeforeUUID,limit);
    }

    public MessageEntity sendMessage(
            UUID conversationId,
            String sender,
            String content) {

        MessageEntity message = new TextMessageEntity();

        message.setConversationId(conversationId);
        message.setSender(sender);
        message.setContent(content);

        return messageRepository.save(message);
    }
}
