package messagingApp.controller.authentication;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.List;

public class CookieVerification extends OncePerRequestFilter {

    private final JwtAuthentificationSecurity jwtService;

    public CookieVerification(JwtAuthentificationSecurity jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain chain) throws ServletException, IOException, java.io.IOException {

        String token = null;

        if (request.getCookies() != null) {
            token = recoverSessionToken(request);
        }

        if (token != null && jwtService.isValid(token)) {
            createAccesAuthentification(token);
        }

        chain.doFilter(request, response);
    }

    private String recoverSessionToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("session"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void createAccesAuthentification(String token){
        String username = jwtService.extractUsername(token);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
