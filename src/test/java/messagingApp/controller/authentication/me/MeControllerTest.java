package messagingApp.controller.authentication.me;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "xavier";
    private static final String EMAIL = "xavier@example.com";

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MeController meController;

    @Test
    void givenNoUserId_whenMe_thenReturnsUnauthorized() {
        when(request.getAttribute("userId")).thenReturn(null);

        ResponseEntity<MeResponse> response = meController.me(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(userService);
    }

    @Test
    void givenUserId_whenMe_thenReturnsUsernameAndEmail() {
        User user = new User(USERNAME, EMAIL, "hash");

        when(request.getAttribute("userId")).thenReturn(USER_ID);
        when(userService.findUserbyId(USER_ID)).thenReturn(user);

        ResponseEntity<MeResponse> response = meController.me(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new MeResponse(USERNAME, EMAIL));
    }
}
