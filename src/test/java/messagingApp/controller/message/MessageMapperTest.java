package messagingApp.controller.message;

import messagingApp.infrastructure.MessageEntity.MessageEntity;
import messagingApp.infrastructure.MessageEntity.TextMessageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageMapperTest {

    private MessageMapper messageMapper;

    @Mock
    private TextMessageEntity textMessageEntity;

    @Mock
    private TextMessageEntity messageEntity;

    private List<MessageEntity> messages;

    private final static UUID ANY_UUID = UUID.randomUUID();
    private final static UUID ANOTHER_UUID = UUID.randomUUID();
    private final static String ANY_SENDER_NAME = "Bob";

    @BeforeEach
    void creatingMessageMapperAndMockMessageEntity() {
        messageMapper = new MessageMapper();
        when(textMessageEntity.getId()).thenReturn(ANY_UUID);
        when(textMessageEntity.getSender()).thenReturn(ANY_SENDER_NAME);
        when(textMessageEntity.getTimestamp()).thenReturn(LocalDateTime.now());
        messages = new ArrayList<>();
        messages.add(textMessageEntity);
    }

    @Test
    void givenCorrectMessage_whenGetMessageFormat_thenMessageIsFromatedMessageResponse() {
        when(textMessageEntity.getContent()).thenReturn("Hello World");

        MessagesResponse messageResponses = messageMapper.getSendMessagesFormat(messages);

        assertEquals(ANY_UUID, messageResponses.messages().getFirst().id());
    }

    @Test
    void givenTextMessage_whenGetMessageFormat_thenTypeIsText(){
        when(textMessageEntity.getContent()).thenReturn("Hello World");

        MessagesResponse messageResponses = messageMapper.getSendMessagesFormat(messages);

        assertEquals("TEXT", messageResponses.messages().getFirst().type());
    }

    @Test
    void givenFakeTypeMessage_whenGetMessageFormat_thenIllegalArgumentException(){
        class FakeTypeMessageEntity extends MessageEntity {
            @Override
            public void setContent(String content) {}
        }
        FakeTypeMessageEntity fakeTypeMessageEntity = new FakeTypeMessageEntity();
        messages.add(fakeTypeMessageEntity);

        assertThrows(IllegalArgumentException.class, () -> messageMapper.getSendMessagesFormat(messages));
    }

    @Test
    void givenSecondMessage_whenGetMessageFormat_OrderStaysTheSame(){
        messages.add(messageEntity);
        when(messageEntity.getId()).thenReturn(ANOTHER_UUID);
        when(messageEntity.getSender()).thenReturn(ANY_SENDER_NAME);
        when(messageEntity.getTimestamp()).thenReturn(LocalDateTime.now().minusDays(1));

        MessagesResponse messageResponses = messageMapper.getSendMessagesFormat(messages);

        assertEquals(ANOTHER_UUID, messageResponses.messages().getLast().id());
        assertEquals(ANY_UUID, messageResponses.messages().getFirst().id());
    }


    @Test
    void givenSecondMessage_whenGetMessageFormat_LastMessageIdIsTheLastOne(){
        messages.add(messageEntity);
        when(messageEntity.getId()).thenReturn(ANOTHER_UUID);
        when(messageEntity.getSender()).thenReturn(ANY_SENDER_NAME);
        when(messageEntity.getTimestamp()).thenReturn(LocalDateTime.now().minusDays(1));

        MessagesResponse messageResponses = messageMapper.getSendMessagesFormat(messages);

        assertEquals(ANOTHER_UUID.toString(), messageResponses.lastMessageId());
        assertEquals("TRUE", messageResponses.conversationDone());
    }


}