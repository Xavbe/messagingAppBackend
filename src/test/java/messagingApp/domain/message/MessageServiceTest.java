package messagingApp.domain.message;

import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import messagingApp.infrastructure.MessageEntity.TextMessageEntity;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageServiceTest {

    @Mock
    MessageRepository messageRepository;

    @Mock
    ConversationService conversationService;

    @Mock
    UserService userService;

    @InjectMocks
    MessageService messageService;

    private final static UUID ANY_CONVERSATION_ID =  UUID.randomUUID();
    private final static UUID ANY_MESSAGE_ID =  UUID.randomUUID();
    private final static Long ANY_USER_ID = 1L;
    private final static String ANY_CONTENT = "Bonjour";
    int ANY_LIMIT = 30;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetMessage_messageRepositoryFindsMessageBefore() {
        messageService.getMessage(ANY_CONVERSATION_ID, ANY_MESSAGE_ID, ANY_LIMIT);

        verify(messageRepository).findMessagesBefore(any(UUID.class), any(UUID.class), any(Pageable.class));
    }

    @Test
    void whenSendMessage_thenMessageIsCompletedAndSaved() {
        Conversation conversation = new Conversation();
        User sender = new User();
        ArgumentCaptor<MessageEntity> messageCaptor = ArgumentCaptor.forClass(MessageEntity.class);

        when(conversationService.getConversationById(ANY_CONVERSATION_ID)).thenReturn(conversation);
        when(userService.findUserbyId(ANY_USER_ID)).thenReturn(sender);
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageEntity savedMessage = messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        verify(messageRepository).save(messageCaptor.capture());
        MessageEntity message = messageCaptor.getValue();
        assertInstanceOf(TextMessageEntity.class, message);
        assertNotNull(message.getId());
        assertEquals(conversation, message.getConversation());
        assertEquals(sender, message.getSender());
        assertEquals(ANY_CONTENT, ((TextMessageEntity) message).getContent());
        assertNotNull(message.getTimestamp());
        assertEquals(message, savedMessage);
    }
}
