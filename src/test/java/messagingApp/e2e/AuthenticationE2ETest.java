package messagingApp.e2e;

import messagingApp.domain.authentication.UserRepository;
import messagingApp.domain.Conversation.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

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

    @Autowired
    private ConversationRepository conversationRepository;

    @BeforeEach
    void cleanDatabase() {
        conversationRepository.deleteAll();
        userRepository.deleteAll();

        restTemplate.getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(HttpStatusCode statusCode) {
                return false;
            }
        });
    }


    private HttpEntity<String> jsonBody(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    private String registerAndLogin(String username, String email, String password) {
        restTemplate.postForEntity(
                "/register",
                jsonBody("""
                        {"username": "%s", "email": "%s", "password": "%s"}
                        """.formatted(username, email, password)),
                String.class
        );

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/login",
                jsonBody("""
                        {"username": "%s", "password": "%s"}
                        """.formatted(username, password)),
                String.class
        );

        String setCookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).isNotNull();
        // Ne garder que "session=xxxxx" (avant le premier ";")
        return setCookie.split(";")[0];
    }

    private HttpEntity<String> withCookie(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        return new HttpEntity<>(null, headers);
    }

    private HttpEntity<String> withCookieAndBody(String cookie, String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(jsonBody, headers);
    }

    @SuppressWarnings("unchecked")
    private String createConversation(String ownerCookie, String name, String... otherUsernames) {
        String usernamesJson = String.join(",",
                java.util.Arrays.stream(otherUsernames).map(u -> "\"" + u + "\"").toList());

        ResponseEntity<Map> response = restTemplate.exchange(
                "/conversations",
                HttpMethod.POST,
                withCookieAndBody(ownerCookie, """
                        {"conversationName": "%s", "usernames": [%s]}
                        """.formatted(name, usernamesJson)),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return (String) response.getBody().get("id");
    }

    // --- Tests ---

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
    void accessConversations_withoutCookie_shouldBeDenied() {
        ResponseEntity<String> response = restTemplate.getForEntity("/conversations", String.class);

        // Ton SecurityConfig actuel renvoie 403 par défaut (Http403ForbiddenEntryPoint)
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

        registerAndLogin("alice","alice@test.com","password123");

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "/register",
                        jsonBody("""
                    {
                        "username":"alice2",
                        "email":"alice@test.com",
                        "password":"password123"
                    }
                    """),
                        String.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_withUnknownUser_shouldBeUnauthorized() {

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "/login",
                        jsonBody("""
                    {
                        "username":"ghost",
                        "password":"password123"
                    }
                    """),
                        String.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createConversation_withoutCookie_shouldBeForbidden() {

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "/conversations",
                        HttpMethod.POST,
                        jsonBody("""
                    {
                        "conversationName":"test",
                        "usernames":[]
                    }
                    """),
                        String.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createConversation_withUnknownUser_shouldFail() {

        String cookie =
                registerAndLogin("alice","alice@test.com","password123");

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "/conversations",
                        HttpMethod.POST,
                        withCookieAndBody(cookie,
                                """
                                {
                                    "conversationName":"Test",
                                    "usernames":["ghost"]
                                }
                                """),
                        String.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void accessConversation_withInvalidCookie_shouldBeForbidden() {

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "/conversations",
                        HttpMethod.GET,
                        withCookie("session=fake-session"),
                        String.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }
}