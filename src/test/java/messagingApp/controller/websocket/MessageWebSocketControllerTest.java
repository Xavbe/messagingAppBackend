package messagingApp.controller.websocket;

import messagingApp.controller.message.MessageResponse;
import messagingApp.domain.message.MessageService;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.mockito.Mockito.*;

class MessageWebSocketControllerTest {

    @Mock private MessageService messageService;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageWebSocketController controller;

    private final UUID CONVERSATION_ID = UUID.randomUUID();
    private final Long USER_ID = 1L;
    private final String CONTENT = "Bonjour";

    private MessageEntity message;
    private CustomUserPrincipal principal;
    private MessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        message = mock(MessageEntity.class);
        principal = new CustomUserPrincipal(USER_ID);
    }

    @Test
    void givenValidRequest_whenSendMessage_thenMessageServiceCalledWithUserId() {
        SendMessageRequest request = new SendMessageRequest(CONVERSATION_ID, CONTENT);

        when(messageService.sendMessage(CONVERSATION_ID, USER_ID, CONTENT))
                .thenReturn(messageResponse);

        controller.sendMessage(request, principal);

        verify(messageService).sendMessage(CONVERSATION_ID, USER_ID, CONTENT);
    }

    @Test
    void givenValidRequest_whenSendMessage_thenCorrectConversationIdUsed() {
        UUID specificId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        SendMessageRequest request = new SendMessageRequest(specificId, CONTENT);

          when(messageService.sendMessage(specificId, USER_ID, CONTENT))
                .thenReturn(messageResponse);

        controller.sendMessage(request, principal);

        verify(messageService).sendMessage(specificId, USER_ID, CONTENT);
    }

    @Test
    void givenValidRequest_whenSendMessage_thenMessagePublishedToConversationTopic() {
        SendMessageRequest request = new SendMessageRequest(CONVERSATION_ID, CONTENT);

        when(messageService.sendMessage(CONVERSATION_ID, USER_ID, CONTENT))
                .thenReturn(messageResponse);

        controller.sendMessage(request, principal);

        verify(messagingTemplate).convertAndSend(
                "/topic/conversations/" + CONVERSATION_ID + "/messages",
                messageResponse
        );
    }
}
