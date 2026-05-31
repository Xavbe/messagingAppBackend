package messagingApp.controller.message;

import messagingApp.domain.message.MessageService;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessagesControllerTest {

    private MessageService messageService = mock(MessageService.class);
    private MessageMapper messageMapper = mock(MessageMapper.class);
    private final static UUID ANY_CONVERSATION_UUID = UUID.randomUUID();
    private final static UUID ANY_MESSAGE_UUID = UUID.randomUUID();
    private final static MessagesResponse GOOD_RESPONSE = new MessagesResponse(List.of(), "TRUE", "123");

    private MessagesController controller;
    private List<MessageEntity> messages;

    @BeforeEach
    void createControllerAndMock() {
        controller = new MessagesController(messageService, messageMapper);
        messages = List.of();
      ;
    }

    @Test
    void givenGoodMessageInfo_whenGetMessages_thenOk() {
        when(messageService.getMessage(ANY_CONVERSATION_UUID, ANY_MESSAGE_UUID, 30)).thenReturn(messages);
        when(messageMapper.getSendMessagesFormat(messages)).thenReturn(GOOD_RESPONSE);

        ResponseEntity<MessagesResponse> result =
                controller.getMessages(ANY_CONVERSATION_UUID.toString(),
                        ANY_MESSAGE_UUID.toString(), 30);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void givenBadAnswer_whenGetMessages_thenError() {
        when(messageService.getMessage(ANY_CONVERSATION_UUID, ANY_MESSAGE_UUID, 30)).thenReturn(messages);
        when(messageMapper.getSendMessagesFormat(messages)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<MessagesResponse> result =
                controller.getMessages(ANY_CONVERSATION_UUID.toString(),
                        ANY_MESSAGE_UUID.toString(), 30);

        assertEquals(500, result.getStatusCode().value());
    }

    @Test
    void givenBadMessageInfo_whenGetMessages_thenError() {
        when(messageService.getMessage(ANY_CONVERSATION_UUID, ANY_MESSAGE_UUID, 30))
                .thenThrow(IllegalArgumentException.class);

        ResponseEntity<MessagesResponse> result =
                controller.getMessages(ANY_CONVERSATION_UUID.toString(),
                        ANY_MESSAGE_UUID.toString(), 30);

        assertEquals(500, result.getStatusCode().value());
    }
}