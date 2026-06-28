package messagingApp;

import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversationTest {

    private Conversation emptyConversation;

    private User user;

    private ArrayList<User> users;

    private final static String USERNAME = "Patrice";
    private final static String PASSWORD = "password";

    @BeforeEach
    void createConversation() {

        users = new ArrayList<>();

        user = new User(USERNAME, PASSWORD);

        emptyConversation = new Conversation(users, LocalDateTime.now());
    }

    @Test
    void whenConversationIsCreated_UserListIsEmpty() {

        assertEquals(0, emptyConversation.getUsers().size());
    }

    @Test
    void whenAddingNewMemberToConversation_ListHasNewMember() {

        emptyConversation.addMember(user);

        assertEquals(1, emptyConversation.getUsers().size());

        assertEquals(USERNAME,
                emptyConversation.getUsers().get(0).getUsername());
    }
}