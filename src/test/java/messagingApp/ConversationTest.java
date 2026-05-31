package messagingApp;

import messagingApp.infrastructure.Conversation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    private Conversation emptyConversation;
    private final static String USERNAME = "Patrice";
    private final static ArrayList<String> USERNAMES = new ArrayList<>();
    LocalDateTime LOCALDATETIME = LocalDateTime.now();

    @BeforeEach
    void createConversation() {
        emptyConversation = new Conversation(USERNAMES,LOCALDATETIME);
    }

    @Test
    void whenConversationIsCreated_UserIsEmpty() {
        assertEquals(0, emptyConversation.getUsernames().size());
    }

    @Test
    void whenAddingNewUUIDMemberToConversation_ListeAsANewMember() {
        emptyConversation.addMember(USERNAME);

        assertEquals(1, emptyConversation.getUsernames().size());

    }
}