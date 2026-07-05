package messagingApp.e2e;

import messagingApp.domain.Conversation.ConversationRepository;
import messagingApp.domain.authentication.UserRepository;
import messagingApp.domain.message.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractE2ETest {
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ConversationRepository conversationRepository;

    @Autowired
    protected MessageRepository messageRepository;

    @BeforeEach
    void cleanDatabase() {
        messageRepository.deleteAll();
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

    protected HttpEntity<String> jsonBody(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    protected String registerAndLogin(String username, String email, String password) {
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
        return setCookie.split(";")[0];
    }

    protected HttpEntity<String> withCookie(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        return new HttpEntity<>(null, headers);
    }

    protected HttpEntity<String> withCookieAndBody(String cookie, String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(jsonBody, headers);
    }

    @SuppressWarnings("unchecked")
    protected String createConversation(String ownerCookie, String name, String... otherUsernames) {
        String usernamesJson = String.join(",",
                Arrays.stream(otherUsernames).map(u -> "\"" + u + "\"").toList());

        ResponseEntity<java.util.Map> response = restTemplate.exchange(
                "/conversations",
                HttpMethod.POST,
                withCookieAndBody(ownerCookie, """
                        {"conversationName": "%s", "usernames": [%s]}
                        """.formatted(name, usernamesJson)),
                java.util.Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return (String) response.getBody().get("id");
    }
}