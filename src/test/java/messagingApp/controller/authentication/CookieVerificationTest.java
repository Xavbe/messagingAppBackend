package messagingApp.controller.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieVerificationTest {

    private final static Cookie COOKIE_WITHOUT_AUTHENTICATION = new Cookie("theme", "dark");
    private final static Cookie COOKIE_WITH_BAD_TOKEN = new Cookie("session", "bad-token");
    private final static Cookie COOKIE_WITH_GOOD_TOKEN = new Cookie("session", "good-token");
    private final static String GOOD_TOKEN = "good-token";
    private final static String BAD_TOKEN = "bad-token";
    private final static Long ANY_USER_ID = 99L;

    @Mock private JwtAuthentificationSecurity jwtService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain chain;
    @InjectMocks private CookieVerification filter;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenNoCookies_thenNoAuth() throws Exception {
        when(request.getCookies()).thenReturn(null);
        filter.doFilterInternal(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void whenNoCookies_thenVerifyChainFilters() throws Exception {
        when(request.getCookies()).thenReturn(null);
        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    void whenNoAuthentificationSessionInCookie_thenNoAuth() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[]{COOKIE_WITHOUT_AUTHENTICATION});
        filter.doFilterInternal(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void whenSessionCookieContainsInvalidToken_thenNoAuth() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[]{COOKIE_WITH_BAD_TOKEN});
        when(jwtService.isValid(BAD_TOKEN)).thenReturn(false);
        filter.doFilterInternal(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void whenValidToken_thenAuthSetInSecurityContext() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[]{COOKIE_WITH_GOOD_TOKEN});
        when(jwtService.isValid(GOOD_TOKEN)).thenReturn(true);
        when(jwtService.extractUserId(GOOD_TOKEN)).thenReturn(ANY_USER_ID);

        filter.doFilterInternal(request, response, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getPrincipal()).isEqualTo(ANY_USER_ID);
    }

    @Test
    void chainIsAlwaysCalled() throws Exception {
        when(request.getCookies()).thenReturn(null);
        filter.doFilterInternal(request, response, chain);
        verify(chain, times(1)).doFilter(request, response);
    }
}