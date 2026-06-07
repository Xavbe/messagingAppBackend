package messagingApp.controller.websocket;

import messagingApp.domain.message.MessageService;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.UUID;

import static org.mockito.Mockito.*;

class MessageWebSocketControllerTest {

    @Mock private MessageService messageService;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private Principal principal;

    @InjectMocks
    private MessageWebSocketController controller;

    private final UUID CONVERSATION_ID = UUID.randomUUID();
    private final String USERNAME = "patrice";
    private final String CONTENT = "Bonjour";

    private final MessageEntity ANY_MESSAGE = new MessageEntity() {
        @Override
        public void setContent(String content) {
        }
    };

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(principal.getName()).thenReturn(USERNAME);
    }

    @Test
    void givenValidRequest_whenSendMessage_thenMessageSavedAndBroadcast() {
        SendMessageRequest request = new SendMessageRequest(CONVERSATION_ID, CONTENT);
        when(messageService.sendMessage(CONVERSATION_ID, USERNAME, CONTENT))
                .thenReturn(ANY_MESSAGE);

        controller.sendMessage(request, principal);

        verify(messageService).sendMessage(CONVERSATION_ID, USERNAME, CONTENT);
        verify(messagingTemplate).convertAndSend(
                "/topic/conversation/" + CONVERSATION_ID,
                ANY_MESSAGE
        );
    }

    @Test
    void givenValidRequest_whenSendMessage_thenCorrectTopicUsed() {
        UUID specificId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        SendMessageRequest request = new SendMessageRequest(specificId, CONTENT);
        when(messageService.sendMessage(any(), any(), any())).thenReturn(ANY_MESSAGE);

        controller.sendMessage(request, principal);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/conversation/00000000-0000-0000-0000-000000000001"),
                any(MessageEntity.class)
        );
    }
}