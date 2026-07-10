package messagingApp.controller.websocket;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.controller.authentication.JwtAuthentificationSecurity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class JwtHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtAuthentificationSecurity jwt;

    public JwtHandshakeHandler(JwtAuthentificationSecurity jwt) {
        this.jwt = jwt;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
            return jwt.extractUserIdFromCookies(req.getCookies())
                    .<Principal>map(CustomUserPrincipal::new)
                    .orElse(null);
        }

        return null;
    }
}
