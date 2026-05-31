package messagingApp.domain.message;

import messagingApp.infrastructure.MessageEntity.MessageEntity;
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
}
