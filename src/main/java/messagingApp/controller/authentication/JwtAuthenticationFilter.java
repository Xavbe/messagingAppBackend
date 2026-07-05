package messagingApp.controller.authentication;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private JwtAuthentificationSecurity jwt;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        Optional<Long> userId = jwt.extractUserIdFromCookies(request.getCookies());
        userId.ifPresent(id -> request.setAttribute("userId", id));

        chain.doFilter(request, servletResponse);
    }
}