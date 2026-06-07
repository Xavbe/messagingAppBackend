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
    void givenValidSessionCookie_whenDetermineUser_thenReturnsPrincipalWithUsername() {
        Cookie sessionCookie = new Cookie("session", "valid-token");
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{sessionCookie});
        when(jwt.isValid("valid-token")).thenReturn(true);
        when(jwt.extractUsername("valid-token")).thenReturn("patrice");

        Principal principal = handler.determineUser(servletRequest, wsHandler, Map.of());

        assertNotNull(principal);
        assertEquals("patrice", principal.getName());
    }

    @Test
    void givenInvalidSessionCookie_whenDetermineUser_thenReturnsNull() {
        Cookie sessionCookie = new Cookie("session", "bad-token");
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{sessionCookie});
        when(jwt.isValid("bad-token")).thenReturn(false);

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