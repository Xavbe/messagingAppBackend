package messagingApp.controller.authentication;

import jakarta.servlet.http.Cookie;
import messagingApp.domain.authentication.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("e2e")
@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc webRequestSimulator;

    @MockBean
    private JwtAuthentificationSecurity jwtService;

    @MockBean
    private UserService userService;

    private final static RequestBuilder loginRequest =post("/login").contentType(MediaType.APPLICATION_JSON)
            .content("""
                  {"username": "test", "password": "test"}
                """);
    private final static RequestBuilder registerRequest =post("/register").contentType(MediaType.APPLICATION_JSON)
            .content("""
                  {"username": "test", "password": "test"}
                """);


    @Test
    void whenLoginPerformed_LoginIsAccessibleWithoutCookie() throws Exception {
        webRequestSimulator.perform(loginRequest)
                .andExpect(status().isOk());
    }

    @Test
    void whenRegisterPerformed_RegisterIsAccessibleWithoutCookie() throws Exception {
        webRequestSimulator.perform(registerRequest)
                .andExpect(status().isOk());
    }

    @Test
    void whenMessagePerformedWithoutToken_thenError403Forbiden() throws Exception {
        webRequestSimulator.perform(get("/messages"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenMessagePerformedWithCorrectToken_thenReturnNotFound() throws Exception {
        when(jwtService.isValid(any())).thenReturn(true);
        when(jwtService.extractUsername(any())).thenReturn("testUser");

        webRequestSimulator.perform(get("/messages")
                        .cookie(new Cookie("session", "valid-jwt")))
                .andExpect(status().isNotFound());
    }

}