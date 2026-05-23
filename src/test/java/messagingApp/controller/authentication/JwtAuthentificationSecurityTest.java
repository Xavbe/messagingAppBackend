package messagingApp.controller.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthentificationSecurityTest {

    private JwtAuthentificationSecurity jwtService;
    private static final String ANY_USERNAME = "John Doe";
    private static final String FAKE_TOLKEN = "123";

    @BeforeEach
    void setUp() {
        jwtService = new JwtAuthentificationSecurity();

        ReflectionTestUtils.setField(
                jwtService,
                "SECRET_KEY",
                "12345678901234567890123456789012"
        );
    }

    @Test
    void whenGenerateToken_ReturnsValidToken() {
        String token = jwtService.generateToken(ANY_USERNAME);

        assertNotNull(token);
        assertTrue(jwtService.isValid(token));
    }

    @Test
    void givenGoodGeneratedToken_whenExtractUsername_ReturnsCorrectUsername() {
        String token = jwtService.generateToken(ANY_USERNAME);

        String username = jwtService.extractUsername(token);

        assertEquals(ANY_USERNAME, username);
    }

    @Test
    void givenInvalidToken_whenIsValid_thenReturnsFalse() {
        assertFalse(jwtService.isValid(FAKE_TOLKEN));
    }
}