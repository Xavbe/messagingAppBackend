package messagingApp.e2e;

import messagingApp.domain.authentication.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void register_persistsUserInDB_andReturnsCookie() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/register",
                jsonBody("alice", "password123"),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User created");

        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(cookie).isNotNull().contains("session=").contains("HttpOnly");

        assertThat(userRepository.findByUsername("alice")).isPresent();
    }

    @Test
    void register_thenLogin_returnsNewSessionCookie() {
        // Étape 1 : créer le compte
        restTemplate.postForEntity("/register", jsonBody("bob", "pass456"), String.class);

        // Étape 2 : se connecter avec le même compte
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/login",
                jsonBody("bob", "pass456"),
                String.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE))
                .isNotNull()
                .contains("session=");
    }

    @Test
    void register_twice_returns401() {
        restTemplate.postForEntity("/register", jsonBody("charlie", "pass"), String.class);

        ResponseEntity<String> secondRegister = restTemplate.postForEntity(
                "/register",
                jsonBody("charlie", "pass"),
                String.class
        );

        assertThat(secondRegister.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(secondRegister.getBody()).isEqualTo("Username already exists");

        // Un seul utilisateur en DB malgré deux tentatives
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void login_nonExistentUser_returns401() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/login",
                jsonBody("ghost", "nopass"),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid username or password");
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNull();
    }


    private HttpEntity<String> jsonBody(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(
                """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password),
                headers
        );
    }
}