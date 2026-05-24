package messagingApp.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getMessage (UUID conversationId, UUID messageBeforeUUID, int limit) {
        return messageRepository.getMessages(conversationId, messageBeforeUUID,limit);
    }
}
