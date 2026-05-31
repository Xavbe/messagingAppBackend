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

    private UUID ANY_UUID = UUID.randomUUID();
    private UUID ANOTHER_UUID = UUID.randomUUID();

    @BeforeEach
    void creatingMessageMapperAndMockMessageEntity() {
        messageMapper = new MessageMapper();
        when(textMessageEntity.getId()).thenReturn(ANY_UUID);
        when(textMessageEntity.getSenderId()).thenReturn(UUID.randomUUID());
        when(textMessageEntity.getTimeStamp()).thenReturn(LocalDateTime.now());
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
        class FakeTypeMessageEntity extends MessageEntity {}
        FakeTypeMessageEntity fakeTypeMessageEntity = new FakeTypeMessageEntity();
        messages.add(fakeTypeMessageEntity);

        assertThrows(IllegalArgumentException.class, () -> messageMapper.getSendMessagesFormat(messages));
    }

    @Test
    void givenSecondMessage_whenGetMessageFormat_OrderStaysTheSame(){
        messages.add(messageEntity);
        when(messageEntity.getId()).thenReturn(ANOTHER_UUID);
        when(messageEntity.getSenderId()).thenReturn(UUID.randomUUID());
        when(messageEntity.getTimeStamp()).thenReturn(LocalDateTime.now().minusDays(1));

        MessagesResponse messageResponses = messageMapper.getSendMessagesFormat(messages);

        assertEquals(ANOTHER_UUID, messageResponses.messages().getLast().id());
        assertEquals(ANY_UUID, messageResponses.messages().getFirst().id());
    }



}