package messagingApp.controller.authentication;

import jakarta.servlet.http.HttpServletResponse;
import messagingApp.domain.authentication.UserAlreadyExists;
import messagingApp.domain.authentication.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    private static final AuthenticationRequest GOOD_USERNAME_REQUEST =
            new AuthenticationRequest("Patrice", "patrice@example.com", "password*!");

    private static final AuthenticationRequest BAD_USERNAME_REQUEST =
            new AuthenticationRequest("Patrice2", "patrice2@example.com", "password2*!");

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void initialiseAuthenticationController() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenGoodCredential_whenLogin_thenSuccess() {
        when(userService.login(GOOD_USERNAME_REQUEST.username(), GOOD_USERNAME_REQUEST.password()))
                .thenReturn("fake-token");

        ResponseEntity<?> response = authenticationController.login(GOOD_USERNAME_REQUEST, httpResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenBadCredential_whenLogout_thenUnauthorizedResponse() {
        doThrow(RuntimeException.class).when(userService).login(BAD_USERNAME_REQUEST.username(),
                BAD_USERNAME_REQUEST.password());

        ResponseEntity<?> response = authenticationController.login(BAD_USERNAME_REQUEST, httpResponse);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void givenGoodInfo_whenRegister_thenSuccess() {
        when(userService.register(GOOD_USERNAME_REQUEST.username(), GOOD_USERNAME_REQUEST.email(),
                GOOD_USERNAME_REQUEST.password()))
                .thenReturn("fake-token");

        ResponseEntity<?> response = authenticationController.register(GOOD_USERNAME_REQUEST, httpResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenBadInfo_whenRegister_thenUnauthorizedResponse() {
        doThrow(UserAlreadyExists.class).when(userService).register(BAD_USERNAME_REQUEST.username(),
                BAD_USERNAME_REQUEST.email(),
                BAD_USERNAME_REQUEST.password());

        ResponseEntity<?> response = authenticationController.register(BAD_USERNAME_REQUEST, httpResponse);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
