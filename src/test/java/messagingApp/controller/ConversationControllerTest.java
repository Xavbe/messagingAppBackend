package messagingApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import messagingApp.controller.conversation.ConversationController;
import messagingApp.controller.conversation.CreateConversationRequest;
import messagingApp.domain.Conversation.ConversationService;
import messagingApp.infrastructure.Conversation;
import org.junit.jupiter.api.BeforeEach;
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
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ConversationController conversationController;

    private static final String ANY_USERNAME = "ALICE";
    private static final List<String> LIST_OF_USERNAMES = List.of("BOB", "Nathan");
    private static final String ANY_CONVERSATION_NAME = "NOM";
    private static final String ATTRIBUTE_FOR_USERNAME = "user";

    private static final List<Conversation> EXPECTED_CONVERSATION = List.of(new Conversation(), new Conversation());

    @BeforeEach
    void createMocks() {
        when(request.getSession()).thenReturn(session);
    }

    @Test
    void givenUsernameNull_whenGetConversations_thenUnauthorized() {
        when(session.getAttribute(ATTRIBUTE_FOR_USERNAME)).thenReturn(null);

        ResponseEntity<List<Conversation>> response = conversationController.getConversations(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void givenUsername_whenGetConversations_thenResponseOk() {
        when(session.getAttribute(ATTRIBUTE_FOR_USERNAME)).thenReturn(ANY_USERNAME);
        when(conversationService.findByUsername(ANY_USERNAME)).thenReturn(EXPECTED_CONVERSATION);

        ResponseEntity<List<Conversation>> response = conversationController.getConversations(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenUsername_whenGetConversations_thenResponseIsExpectedConversation(){
        when(session.getAttribute(ATTRIBUTE_FOR_USERNAME)).thenReturn(ANY_USERNAME);
        when(conversationService.findByUsername(ANY_USERNAME)).thenReturn(EXPECTED_CONVERSATION);

        ResponseEntity<List<Conversation>> response = conversationController.getConversations(request);

        assertThat(response.getBody()).isEqualTo(EXPECTED_CONVERSATION);
    }

    @Test
    void givenUsername_whenGetConversations_thenResponseIsExpectedAnyUsername(){
        when(session.getAttribute(ATTRIBUTE_FOR_USERNAME)).thenReturn(ANY_USERNAME);
        when(conversationService.findByUsername(ANY_USERNAME)).thenReturn(EXPECTED_CONVERSATION);

        ResponseEntity<List<Conversation>> response = conversationController.getConversations(request);

        verify(conversationService).findByUsername(ANY_USERNAME);
    }


    @Test
    void givenConversations_whenUserHasNoConversations_thenShouldReturnEmptyList() {
        when(session.getAttribute(ATTRIBUTE_FOR_USERNAME)).thenReturn(ANY_USERNAME);
        when(conversationService.findByUsername(ANY_USERNAME)).thenReturn(List.of());

        ResponseEntity<List<Conversation>> response = conversationController.getConversations(request);

        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void givenNoSessionUser_whenCreateConversation_thenReturns401() {
        when(session.getAttribute("user")).thenReturn(null);
        CreateConversationRequest body = new CreateConversationRequest(ANY_CONVERSATION_NAME, List.of("bob"));

        ResponseEntity<Conversation> response = conversationController.createConversation(body, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verifyNoInteractions(conversationService);
    }

    @Test
    void givenSessionUser_whenCreateConversation_thenServiceCalledWithCurrentUserAndUsernames() {
        when(session.getAttribute("user")).thenReturn(ANY_USERNAME);
        CreateConversationRequest body = new CreateConversationRequest(ANY_CONVERSATION_NAME, LIST_OF_USERNAMES);

        ResponseEntity<Conversation> response = conversationController.createConversation(body, request);

        verify(conversationService).createConversation(ANY_CONVERSATION_NAME, ANY_USERNAME, LIST_OF_USERNAMES);
    }
}