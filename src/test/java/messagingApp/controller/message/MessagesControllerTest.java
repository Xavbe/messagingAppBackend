package messagingApp.controller.message;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.message.MessageService;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagesControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private ConversationService conversationService;

    @Mock
    private HttpServletRequest request;

    private static final UUID ANY_CONVERSATION_UUID = UUID.randomUUID();
    private static final UUID ANY_MESSAGE_UUID = UUID.randomUUID();
    private static final Long ANY_USER_ID = 1L;
    private static final MessagesResponse GOOD_RESPONSE = new MessagesResponse(List.of(), "TRUE", "123");

    private MessagesController controller;
    private List<MessageEntity> messages;

    @BeforeEach
    void createController() {
        controller = new MessagesController(messageService, messageMapper, conversationService);
        messages = List.of();
    }

    @Test
    void givenNoUserId_whenGetMessages_thenUnauthorized() {
        when(request.getAttribute("userId")).thenReturn(null);

        ResponseEntity<MessagesResponse> result = controller.getMessages(
                ANY_CONVERSATION_UUID.toString(), ANY_MESSAGE_UUID.toString(), 30, request);

        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    void givenUserNotMemberOfConversation_whenGetMessages_thenForbidden() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(conversationService.isMember(ANY_CONVERSATION_UUID, ANY_USER_ID)).thenReturn(false);

        ResponseEntity<MessagesResponse> result = controller.getMessages(
                ANY_CONVERSATION_UUID.toString(), ANY_MESSAGE_UUID.toString(), 30, request);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    void givenGoodMessageInfo_whenGetMessages_thenOk() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(conversationService.isMember(ANY_CONVERSATION_UUID, ANY_USER_ID)).thenReturn(true);
        when(messageService.getMessage(ANY_CONVERSATION_UUID, ANY_MESSAGE_UUID, 30)).thenReturn(messages);
        when(messageMapper.getSendMessagesFormat(messages)).thenReturn(GOOD_RESPONSE);

        ResponseEntity<MessagesResponse> result = controller.getMessages(
                ANY_CONVERSATION_UUID.toString(), ANY_MESSAGE_UUID.toString(), 30, request);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void givenMapperThrows_whenGetMessages_thenError500() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(conversationService.isMember(ANY_CONVERSATION_UUID, ANY_USER_ID)).thenReturn(true);
        when(messageService.getMessage(ANY_CONVERSATION_UUID, ANY_MESSAGE_UUID, 30)).thenReturn(messages);
        when(messageMapper.getSendMessagesFormat(messages)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<MessagesResponse> result = controller.getMessages(
                ANY_CONVERSATION_UUID.toString(), ANY_MESSAGE_UUID.toString(), 30, request);

        assertEquals(500, result.getStatusCode().value());
    }

    @Test
    void givenServiceThrows_whenGetMessages_thenError500() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(conversationService.isMember(ANY_CONVERSATION_UUID, ANY_USER_ID)).thenReturn(true);
        when(messageService.getMessage(ANY_CONVERSATION_UUID, ANY_MESSAGE_UUID, 30))
                .thenThrow(IllegalArgumentException.class);

        ResponseEntity<MessagesResponse> result = controller.getMessages(
                ANY_CONVERSATION_UUID.toString(), ANY_MESSAGE_UUID.toString(), 30, request);

        assertEquals(500, result.getStatusCode().value());
    }
}