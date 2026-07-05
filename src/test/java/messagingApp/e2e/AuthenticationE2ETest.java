package messagingApp.e2e;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
class AuthenticationE2ETest extends AbstractE2ETest {

    @Test
    void register_persistsUserInDB_andReturnsCookie() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/register",
                jsonBody("""
                        {"username": "alice", "email": "alice@example.com", "password": "password123"}
                        """),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User created");

        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(cookie).isNotNull().contains("session=").contains("HttpOnly");

        assertThat(userRepository.findByUsername("alice")).isPresent();
    }

    @Test
    void login_withWrongPassword_shouldBeUnauthorized() {
        restTemplate.postForEntity(
                "/register",
                jsonBody("""
                        {"username": "bob", "email": "bob@example.com", "password": "password123"}
                        """),
                String.class
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/login",
                jsonBody("""
                        {"username": "bob", "password": "wrong-password"}
                        """),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_withUnknownUser_shouldBeUnauthorized() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/login",
                jsonBody("""
                        {"username": "ghost", "password": "password123"}
                        """),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void register_withDuplicateUsername_shouldFail() {
        registerAndLogin("ivan", "ivan@example.com", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/register",
                jsonBody("""
                        {"username": "ivan", "email": "ivan2@example.com", "password": "password456"}
                        """),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void register_withDuplicateEmail_shouldFail() {
        registerAndLogin("alice", "alice@test.com", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/register",
                jsonBody("""
                        {"username": "alice2", "email": "alice@test.com", "password": "password123"}
                        """),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}