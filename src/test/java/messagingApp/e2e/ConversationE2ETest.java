package messagingApp.e2e;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
class ConversationE2ETest extends AbstractE2ETest {

    @Test
    void accessConversations_withoutCookie_shouldBeDenied() {
        ResponseEntity<String> response = restTemplate.getForEntity("/conversations", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void accessConversations_withValidCookie_shouldSucceed() {
        String cookie = registerAndLogin("carla", "carla@example.com", "password123");

        ResponseEntity<String> response = restTemplate.exchange(
                "/conversations", HttpMethod.GET, withCookie(cookie), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void accessConversation_withInvalidCookie_shouldBeForbidden() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/conversations", HttpMethod.GET, withCookie("session=fake-session"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createConversation_withoutCookie_shouldBeForbidden() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/conversations",
                HttpMethod.POST,
                jsonBody("""
                        {"conversationName": "test", "usernames": []}
                        """),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createConversation_withUnknownUser_shouldFail() {
        String cookie = registerAndLogin("alice", "alice@test.com", "password123");

        ResponseEntity<String> response = restTemplate.exchange(
                "/conversations",
                HttpMethod.POST,
                withCookieAndBody(cookie, """
                        {"conversationName": "Test", "usernames": ["ghost"]}
                        """),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}