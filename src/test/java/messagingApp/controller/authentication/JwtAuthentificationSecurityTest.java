package messagingApp.controller.authentication;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAuthentificationSecurityTest {

    private final JwtAuthentificationSecurity jwt = new JwtAuthentificationSecurity();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                jwt,
                "SECRET_KEY",
                "0123456789012345678901234567890123456789012345678901234567890123"
        );
    }

    @Test
    void givenNullCookies_whenExtractUserIdFromCookies_thenReturnsEmpty() {
        Optional<Long> result = jwt.extractUserIdFromCookies(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenCookiesWithoutSessionCookie_whenExtractUserIdFromCookies_thenReturnsEmpty() {
        Cookie[] cookies = new Cookie[]{new Cookie("theme", "dark")};

        Optional<Long> result = jwt.extractUserIdFromCookies(cookies);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenValidSessionCookie_whenExtractUserIdFromCookies_thenReturnsUserId() {
        String token = jwt.generateToken(42L);
        Cookie[] cookies = new Cookie[]{new Cookie("session", token)};

        Optional<Long> result = jwt.extractUserIdFromCookies(cookies);

        assertEquals(Optional.of(42L), result);
    }

    @Test
    void givenInvalidSessionCookie_whenExtractUserIdFromCookies_thenReturnsEmpty() {
        Cookie[] cookies = new Cookie[]{new Cookie("session", "not-a-real-token")};

        Optional<Long> result = jwt.extractUserIdFromCookies(cookies);

        assertTrue(result.isEmpty());
    }
}