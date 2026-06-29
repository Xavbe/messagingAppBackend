package messagingApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.controller.conversation.ConversationController;
import messagingApp.controller.conversation.CreateConversationRequest;
import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationControllerTest {

    @Mock
    private ConversationService conversationService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ConversationController conversationController;

    private static final Long ANY_USER_ID = 1L;
    private static final String ANY_USERNAME = "ALICE";
    private static final List<String> LIST_OF_USERNAMES = List.of("BOB", "Nathan");
    private static final String ANY_CONVERSATION_NAME = "NOM";

    private static final List<Conversation> EXPECTED_CONVERSATION =
            List.of(new Conversation(), new Conversation());

    private User createUser() {
        User user = new User();
        user.setUsername(ANY_USERNAME);
        return user;
    }

    @Test
    void givenUserIdNull_whenGetConversations_thenUnauthorized() {
        when(request.getAttribute("userId")).thenReturn(null);

        ResponseEntity<List<Conversation>> response =
                conversationController.getConversations(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void givenUserId_whenGetConversations_thenResponseOk() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(userService.findUserbyId(ANY_USER_ID)).thenReturn(createUser());
        when(conversationService.findByUsername(ANY_USERNAME))
                .thenReturn(EXPECTED_CONVERSATION);

        ResponseEntity<List<Conversation>> response =
                conversationController.getConversations(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenUserId_whenGetConversations_thenReturnsExpectedConversation() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(userService.findUserbyId(ANY_USER_ID)).thenReturn(createUser());
        when(conversationService.findByUsername(ANY_USERNAME))
                .thenReturn(EXPECTED_CONVERSATION);

        ResponseEntity<List<Conversation>> response =
                conversationController.getConversations(request);

        assertThat(response.getBody()).isEqualTo(EXPECTED_CONVERSATION);
    }

    @Test
    void givenUserId_whenGetConversations_thenServiceCalledWithUsername() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(userService.findUserbyId(ANY_USER_ID)).thenReturn(createUser());
        when(conversationService.findByUsername(ANY_USERNAME))
                .thenReturn(EXPECTED_CONVERSATION);

        conversationController.getConversations(request);

        verify(conversationService).findByUsername(ANY_USERNAME);
    }

    @Test
    void givenNoConversation_whenGetConversations_thenReturnsEmptyList() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(userService.findUserbyId(ANY_USER_ID)).thenReturn(createUser());
        when(conversationService.findByUsername(ANY_USERNAME))
                .thenReturn(List.of());

        ResponseEntity<List<Conversation>> response =
                conversationController.getConversations(request);

        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void givenNoUserId_whenCreateConversation_thenReturns401() {
        when(request.getAttribute("userId")).thenReturn(null);

        CreateConversationRequest body =
                new CreateConversationRequest(ANY_CONVERSATION_NAME, List.of("bob"));

        ResponseEntity<Conversation> response =
                conversationController.createConversation(body, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verifyNoInteractions(conversationService);
        verifyNoInteractions(userService);
    }

    @Test
    void givenUserId_whenCreateConversation_thenServiceCalled() {
        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(userService.findUserbyId(ANY_USER_ID)).thenReturn(createUser());

        CreateConversationRequest body =
                new CreateConversationRequest(ANY_CONVERSATION_NAME, LIST_OF_USERNAMES);

        conversationController.createConversation(body, request);

        verify(conversationService).createConversation(
                ANY_CONVERSATION_NAME,
                ANY_USERNAME,
                LIST_OF_USERNAMES
        );
    }

    @Test
    void givenUserId_whenCreateConversation_thenReturnsCreated() {
        Conversation conversation = new Conversation();

        when(request.getAttribute("userId")).thenReturn(ANY_USER_ID);
        when(userService.findUserbyId(ANY_USER_ID)).thenReturn(createUser());
        when(conversationService.createConversation(
                ANY_CONVERSATION_NAME,
                ANY_USERNAME,
                LIST_OF_USERNAMES))
                .thenReturn(conversation);

        CreateConversationRequest body =
                new CreateConversationRequest(ANY_CONVERSATION_NAME, LIST_OF_USERNAMES);

        ResponseEntity<Conversation> response =
                conversationController.createConversation(body, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(conversation);
    }
}