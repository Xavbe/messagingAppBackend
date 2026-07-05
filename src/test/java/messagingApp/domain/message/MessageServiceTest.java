package messagingApp.domain.message;

import messagingApp.controller.message.MessageMapper;
import messagingApp.controller.message.MessageResponse;
import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import messagingApp.infrastructure.MessageEntity.TextMessageEntity;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationService conversationService;

    @Mock
    private UserService userService;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageService messageService;

    private static final UUID ANY_CONVERSATION_ID = UUID.randomUUID();
    private static final Long ANY_USER_ID = 1L;
    private static final String ANY_CONTENT = "Hello there!";

    private Conversation conversation;
    private User sender;

    @BeforeEach
    void setUp() {
        conversation = new Conversation();
        sender = new User();

        when(conversationService.getConversationById(ANY_CONVERSATION_ID)).thenReturn(conversation);
        when(userService.findUserbyId(ANY_USER_ID)).thenReturn(sender);
        when(messageRepository.save(any(MessageEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void sendMessage_createsTextMessageEntity() {
        messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        ArgumentCaptor<MessageEntity> captor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository).save(captor.capture());

        assertInstanceOf(TextMessageEntity.class, captor.getValue());
    }

    @Test
    void sendMessage_setsGeneratedId() {
        messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        ArgumentCaptor<MessageEntity> captor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository).save(captor.capture());

        assertNotNull(captor.getValue().getId());
    }

    @Test
    void sendMessage_attachesCorrectConversation() {
        messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        ArgumentCaptor<MessageEntity> captor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository).save(captor.capture());

        assertEquals(conversation, captor.getValue().getConversation());
    }

    @Test
    void sendMessage_attachesCorrectSender() {
        messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        ArgumentCaptor<MessageEntity> captor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository).save(captor.capture());

        assertEquals(sender, captor.getValue().getSender());
    }

    @Test
    void sendMessage_setsProvidedContent() {
        messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        ArgumentCaptor<MessageEntity> captor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository).save(captor.capture());

        TextMessageEntity saved = (TextMessageEntity) captor.getValue();
        assertEquals(ANY_CONTENT, saved.getContent());
    }

    @Test
    void sendMessage_setsNonNullTimestamp() {
        messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        ArgumentCaptor<MessageEntity> captor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository).save(captor.capture());

        assertNotNull(captor.getValue().getTimestamp());
    }

    @Test
    void sendMessage_returnsMappedResponseFromMapper() {
        MessageResponse expectedResponse =
                new MessageResponse(UUID.randomUUID(), ANY_CONTENT, "TEXT", "someUser", LocalDateTime.now());

        when(messageMapper.getSendMessageFormat(any(MessageEntity.class)))
                .thenReturn(expectedResponse);

        MessageResponse actualResponse =
                messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void sendMessage_passesSavedEntityToMapper() {
        messageService.sendMessage(ANY_CONVERSATION_ID, ANY_USER_ID, ANY_CONTENT);

        ArgumentCaptor<MessageEntity> savedCaptor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageMapper).getSendMessageFormat(savedCaptor.capture());

        assertInstanceOf(TextMessageEntity.class, savedCaptor.getValue());
    }
}
