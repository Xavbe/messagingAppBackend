package messagingApp.controller.websocket;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import messagingApp.controller.authentication.JwtAuthentificationSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtHandshakeHandlerTest {

    private static final Long ANY_USER_ID = 7L;

    @Mock private JwtAuthentificationSecurity jwt;
    @Mock private ServletServerHttpRequest servletRequest;
    @Mock private HttpServletRequest httpRequest;
    @Mock private WebSocketHandler wsHandler;

    private JwtHandshakeHandler handler;

    @BeforeEach
    void setUp() {
        handler = new JwtHandshakeHandler(jwt);
    }

    @Test
    void givenCookiesYieldUserId_whenDetermineUser_thenReturnsPrincipalWithUserId() {
        Cookie[] cookies = new Cookie[]{new Cookie("session", "good-token")};
        when(servletRequest.getServletRequest()).thenReturn(httpRequest);
        when(httpRequest.getCookies()).thenReturn(cookies);
        when(jwt.extractUserIdFromCookies(cookies)).thenReturn(Optional.of(ANY_USER_ID));

        Principal principal = handler.determineUser(servletRequest, wsHandler, Map.of());

        assertNotNull(principal);
        assertInstanceOf(CustomUserPrincipal.class, principal);
        assertEquals(ANY_USER_ID, ((CustomUserPrincipal) principal).getUserId());
        assertEquals(ANY_USER_ID.toString(), principal.getName());
    }

    @Test
    void givenCookiesYieldNoUserId_whenDetermineUser_thenReturnsNull() {
        Cookie[] cookies = new Cookie[]{new Cookie("theme", "dark")};
        when(httpRequest.getCookies()).thenReturn(cookies);
        when(jwt.extractUserIdFromCookies(cookies)).thenReturn(Optional.empty());
        when(servletRequest.getServletRequest()).thenReturn(httpRequest);


        Principal principal = handler.determineUser(servletRequest, wsHandler, Map.of());

        assertNull(principal);
    }

    @Test
    void givenNonServletRequest_whenDetermineUser_thenReturnsNullWithoutCallingJwt() {
        ServerHttpRequest nonServletRequest = mock(ServerHttpRequest.class);

        Principal principal = handler.determineUser(nonServletRequest, wsHandler, Map.of());

        assertNull(principal);
        verifyNoInteractions(jwt);
    }
}