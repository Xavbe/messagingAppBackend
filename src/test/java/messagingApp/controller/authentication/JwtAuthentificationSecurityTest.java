package messagingApp.controller.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthentificationSecurityTest {

    private JwtAuthentificationSecurity jwtService;
    private static final Long ANY_USER_ID = 42L;
    private static final String FAKE_TOKEN = "123";

    @BeforeEach
    void setUp() {
        jwtService = new JwtAuthentificationSecurity();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", "12345678901234567890123456789012");
    }

    @Test
    void whenGenerateToken_ReturnsValidToken() {
        String token = jwtService.generateToken(ANY_USER_ID);

        assertNotNull(token);
        assertTrue(jwtService.isValid(token));
    }

    @Test
    void givenGoodGeneratedToken_whenExtractUserId_ReturnsCorrectId() {
        String token = jwtService.generateToken(ANY_USER_ID);

        Long userId = jwtService.extractUserId(token);

        assertEquals(ANY_USER_ID, userId);
    }

    @Test
    void givenInvalidToken_whenIsValid_thenReturnsFalse() {
        assertFalse(jwtService.isValid(FAKE_TOKEN));
    }
}