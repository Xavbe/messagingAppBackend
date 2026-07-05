package messagingApp.domain.message;

import messagingApp.controller.message.MessageMapper;
import messagingApp.controller.message.MessageResponse;
import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import messagingApp.infrastructure.MessageEntity.TextMessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private MessageMapper messageMapper;

    public List<MessageEntity> getMessage(UUID conversationId, UUID messageBeforeUUID, int limit) {
        if (messageBeforeUUID == null) {
            return messageRepository.findLatestMessages(conversationId, PageRequest.of(0, limit));
        }
        return messageRepository.findMessagesBefore(conversationId, messageBeforeUUID, PageRequest.of(0, limit));
    }

    @Transactional
    public MessageResponse sendMessage(UUID conversationId, long senderId, String content) {

        MessageEntity message = new TextMessageEntity();

        message.setId(UUID.randomUUID());
        message.setConversation(conversationService.getConversationById(conversationId));
        message.setSender(userService.findUserbyId(senderId));
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        MessageEntity saved = messageRepository.save(message);

        return messageMapper.getSendMessageFormat(saved);
    }
}