package messagingApp.controller.websocket;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import messagingApp.controller.authentication.JwtAuthentificationSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtHandshakeHandlerTest {

    private final static String GOOD_TOKEN = "good-token";
    private final static String BAD_TOKEN = "bad-token";
    private final static Long ANY_USER_ID = 7L;

    @Mock private JwtAuthentificationSecurity jwt;
    @Mock private ServletServerHttpRequest servletRequest;
    @Mock private HttpServletRequest httpRequest;
    @Mock private WebSocketHandler wsHandler;

    private JwtHandshakeHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new JwtHandshakeHandler(jwt);
        when(servletRequest.getServletRequest()).thenReturn(httpRequest);
    }

    @Test
    void givenValidSessionCookie_whenDetermineUser_thenReturnsPrincipalWithUserId() {
        Cookie sessionCookie = new Cookie("session", GOOD_TOKEN);
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{sessionCookie});
        when(jwt.isValid(GOOD_TOKEN)).thenReturn(true);
        when(jwt.extractUserId(GOOD_TOKEN)).thenReturn(ANY_USER_ID);

        Principal principal = handler.determineUser(servletRequest, wsHandler, Map.of());

        assertNotNull(principal);
        assertInstanceOf(CustomUserPrincipal.class, principal);
        assertEquals(ANY_USER_ID.toString(), principal.getName());
        assertEquals(ANY_USER_ID, ((CustomUserPrincipal) principal).getUserId());
    }

    @Test
    void givenInvalidSessionCookie_whenDetermineUser_thenReturnsNull() {
        Cookie sessionCookie = new Cookie("session", BAD_TOKEN);
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{sessionCookie});
        when(jwt.isValid(BAD_TOKEN)).thenReturn(false);

        Principal principal = handler.determineUser(servletRequest, wsHandler, Map.of());

        assertNull(principal);
    }

    @Test
    void givenNoCookies_whenDetermineUser_thenReturnsNull() {
        when(httpRequest.getCookies()).thenReturn(null);

        Principal principal = handler.determineUser(servletRequest, wsHandler, Map.of());

        assertNull(principal);
    }

    @Test
    void givenCookiesWithoutSessionCookie_whenDetermineUser_thenReturnsNull() {
        Cookie other = new Cookie("theme", "dark");
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{other});

        Principal principal = handler.determineUser(servletRequest, wsHandler, Map.of());

        assertNull(principal);
        verify(jwt, never()).isValid(any());
    }
}
