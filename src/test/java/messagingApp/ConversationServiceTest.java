package messagingApp;

import messagingApp.domain.Conversation.ConversationRepository;
import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.authentication.UserService;
import messagingApp.domain.authentication.UsernameNotFoundException;
import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.fasterxml.jackson.annotation.PropertyAccessor.CREATOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    ConversationRepository conversationRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ConversationService conversationService;

    private static final String CREATOR = "alice";
    private static final String OTHER = "bob";
    private User creator;
    private User other;
    private static final String ANY_USERNAME = "Martin";
    private static final String ANY_CONVERSATION_NAME = "Conversation";
    private static final Long ANY_USER_ID = 123L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        creator = mock(User.class);
        other = mock(User.class);
    }

    @Test
    void whenFindByUserName_thenRepositoryGetsAllConversations() {
        when(userService.findByUsername(ANY_USERNAME)).thenReturn(creator);
        when(creator.getId()).thenReturn(ANY_USER_ID);

        conversationService.findByUsername(ANY_USERNAME);

        verify(conversationRepository).getAllConversationsForUserId(ANY_USER_ID);
    }

    @Test
    void givenValidUsernames_whenCreateConversation_thenSavesConversationWithAllParticipants() {
        when(userService.findByUsername(CREATOR)).thenReturn(creator);
        when(userService.findByUsername(OTHER)).thenReturn(other);
        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Conversation result = conversationService.createConversation(ANY_CONVERSATION_NAME, CREATOR, List.of(OTHER));

        assertEquals(2, result.getUsers().size());
        assertTrue(result.getUsers().contains(creator));
        assertTrue(result.getUsers().contains(other));
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    void givenDuplicateUsernameAsCreator_whenCreateConversation_thenParticipantNotAddedTwice() {
        when(userService.findByUsername(CREATOR)).thenReturn(creator);
        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Conversation result = conversationService.createConversation(ANY_CONVERSATION_NAME, CREATOR, List.of(CREATOR));

        assertEquals(1, result.getUsers().size());
    }

    @Test
    void givenUnknownUsername_whenCreateConversation_thenThrowsUsernameNotFoundException() {
        when(userService.findByUsername(CREATOR)).thenReturn(creator);
        when(userService.findByUsername(OTHER)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class,
                () -> conversationService.createConversation(ANY_CONVERSATION_NAME, CREATOR, List.of(OTHER)));

        verify(conversationRepository, never()).save(any());
    }

    @Test
    void givenEmptyUsernamesList_whenCreateConversation_thenConversationContainsOnlyCreator() {
        when(userService.findByUsername(CREATOR)).thenReturn(creator);
        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Conversation result = conversationService.createConversation(ANY_CONVERSATION_NAME, CREATOR, List.of());

        assertEquals(1, result.getUsers().size());
    }
}