package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    private Conversation emptyConversation;
    private final static UUID ANY_UUID = UUID.randomUUID();

    @BeforeEach
    void createConversation (){
        emptyConversation = new Conversation();
    }

    @Test
    void whenConversationIsCreated_UserIsEmpty(){
        assertEquals(0, emptyConversation.getUsersId().size());
    }

    @Test
    void whenAddingNewUUIDMemberToConversation_ListeAsANewMember() {
        emptyConversation.addMember(ANY_UUID);

        assertEquals(1, emptyConversation.getUsersId().size());

    }
}