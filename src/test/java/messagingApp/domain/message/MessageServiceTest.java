package messagingApp.domain.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class MessageServiceTest {

    @Mock
    MessageRepository messageRepository;

    @InjectMocks
    MessageService messageService;

    private final static UUID ANY_CONVERSATION_ID =  UUID.randomUUID();
    private final static UUID ANY_MESSAGE_ID =  UUID.randomUUID();
    int ANY_LIMIT = 30;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetMessage_messageRepositoryFindsMessageBefore() {
        messageService.getMessage(ANY_CONVERSATION_ID, ANY_MESSAGE_ID, ANY_LIMIT);

        verify(messageRepository).findMessagesBefore(ANY_CONVERSATION_ID, ANY_MESSAGE_ID, ANY_LIMIT);
    }
}