package messagingApp.e2e;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
class MessageE2ETest extends AbstractE2ETest {

    @Test
    void accessMessages_asConversationMember_shouldSucceed() {
        String ownerCookie = registerAndLogin("dave", "dave@example.com", "password123");
        registerAndLogin("erin", "erin@example.com", "password123");

        String conversationId = createConversation(ownerCookie, "Test Convo", "erin");

        ResponseEntity<String> response = restTemplate.exchange(
                "/conversations/" + conversationId + "/messages?limit=30",
                HttpMethod.GET, withCookie(ownerCookie), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void accessMessages_asNonMember_shouldBeForbidden() {
        String ownerCookie = registerAndLogin("frank", "frank@example.com", "password123");
        registerAndLogin("grace", "grace@example.com", "password123");
        String intruderCookie = registerAndLogin("intruder", "intruder@example.com", "password123");

        String conversationId = createConversation(ownerCookie, "Private Convo", "grace");

        ResponseEntity<String> response = restTemplate.exchange(
                "/conversations/" + conversationId + "/messages?limit=30",
                HttpMethod.GET, withCookie(intruderCookie), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void accessMessages_withoutMessageBeforeUUID_shouldNotFail() {
        String cookie = registerAndLogin("henry", "henry@example.com", "password123");
        String conversationId = createConversation(cookie, "Solo test");

        ResponseEntity<String> response = restTemplate.exchange(
                "/conversations/" + conversationId + "/messages?limit=30",
                HttpMethod.GET, withCookie(cookie), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}