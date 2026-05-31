package messagingApp;

import messagingApp.domain.Conversation.ConversationRepository;
import messagingApp.domain.Conversation.ConversationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    ConversationRepository conversationRepository;

    @InjectMocks
    ConversationService conversationService;

    private static final String ANY_USERNAME = "Martin";

    @Test
    void whenFindByUserName_thenRepositoryGetsAllConversations() {
        conversationService.findByUsername(ANY_USERNAME);

        verify(conversationRepository).getAllConversationsforUsername(ANY_USERNAME);
    }

}