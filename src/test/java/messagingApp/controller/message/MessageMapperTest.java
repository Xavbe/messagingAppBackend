package messagingApp.controller.message;

import messagingApp.infrastructure.MessageEntity.MessageEntity;
import messagingApp.infrastructure.MessageEntity.TextMessageEntity;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageMapperTest {

    private MessageMapper messageMapper;
    private User user;
    private List<MessageEntity> messages;

    private final static UUID UUID_1 = UUID.randomUUID();
    private final static UUID UUID_2 = UUID.randomUUID();
    private final static String SENDER = "Bob";

    @BeforeEach
    void setup() {
        user  = new User();
        user.setUsername(SENDER);
        messageMapper = new MessageMapper();
        messages = new ArrayList<>();
    }

    private TextMessageEntity createMessage(UUID id, String content, LocalDateTime time) {
        TextMessageEntity msg = new TextMessageEntity();
        msg.setId(id);
        msg.setContent(content);
        msg.setSender(user);
        msg.setTimestamp(time);
        return msg;
    }

    @Test
    void givenCorrectMessage_whenMapping_thenMessageIsFormatted() {

        messages.add(createMessage(UUID_1, "Hello", LocalDateTime.now()));

        MessagesResponse response = messageMapper.getSendMessagesFormat(messages);

        assertEquals(UUID_1, response.messages().getFirst().id());
    }

    @Test
    void givenTextMessage_whenMapping_thenTypeIsText() {

        messages.add(createMessage(UUID_1, "Hello", LocalDateTime.now()));

        MessagesResponse response = messageMapper.getSendMessagesFormat(messages);

        assertEquals("TEXT", response.messages().getFirst().type());
    }

    @Test
    void givenFakeTypeMessage_whenMapping_thenThrowsException() {

        class FakeMessage extends MessageEntity {
            @Override
            public void setContent(String content) {}
        }

        FakeMessage fake = new FakeMessage();
        fake.setId(UUID_1);
        fake.setTimestamp(LocalDateTime.now());

        messages.add(fake);

        assertThrows(IllegalArgumentException.class,
                () -> messageMapper.getSendMessagesFormat(messages));
    }

    @Test
    void givenTwoMessages_whenMapping_thenOrderIsPreserved() {

        messages.add(createMessage(UUID_1, "A", LocalDateTime.now()));
        messages.add(createMessage(UUID_2, "B", LocalDateTime.now().minusDays(1)));

        MessagesResponse response = messageMapper.getSendMessagesFormat(messages);

        assertEquals(UUID_1, response.messages().get(0).id());
        assertEquals(UUID_2, response.messages().get(1).id());
    }

    @Test
    void givenTwoMessages_whenMapping_thenLastMessageIsCorrect() {

        messages.add(createMessage(UUID_1, "A", LocalDateTime.now()));
        messages.add(createMessage(UUID_2, "B", LocalDateTime.now().minusDays(1)));

        MessagesResponse response = messageMapper.getSendMessagesFormat(messages);

        assertEquals(UUID_2.toString(), response.lastMessageId());
        assertEquals("TRUE", response.conversationDone());
    }
}