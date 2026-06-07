package messagingApp.controller.websocket;

import jakarta.servlet.http.Cookie;
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
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {

            HttpServletRequest req = servletRequest.getServletRequest();

            Cookie[] cookies = req.getCookies();

            if (cookies != null) {

                for (Cookie cookie : cookies) {

                    if ("session".equals(cookie.getName())) {

                        String token = cookie.getValue();

                        if (jwt.isValid(token)) {

                            String username =
                                    jwt.extractUsername(token);

                            return () -> username;
                        }
                    }
                }
            }
        }

        return null;
    }
}