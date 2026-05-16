package controller;

import domain.authentication.UserService;
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

    private static final String GOOD_USER_NAME = "Patrice";
    private static final String GOOD_USER_PASSWORD = "password*!";

    private static final String BAD_USER_NAME = "Patrice";
    private static final String BAD_USER_PASSWORD = "password*!";


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
        doNothing().when(userService).login(GOOD_USER_NAME, GOOD_USER_PASSWORD);

        ResponseEntity<String> response = authenticationController.login(GOOD_USER_NAME, GOOD_USER_PASSWORD);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenBadCredential_whenLogout_thenUnauthorizedResponse() {
        doThrow(RuntimeException.class).when(userService).login(BAD_USER_NAME, BAD_USER_PASSWORD);

        ResponseEntity<String> response = authenticationController.login(BAD_USER_NAME, BAD_USER_PASSWORD);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void givenGoodInfo_whenRegister_thenSuccess() {
        doNothing().when(userService).register(GOOD_USER_NAME, GOOD_USER_PASSWORD);

        ResponseEntity<String> response = authenticationController.register(GOOD_USER_NAME, GOOD_USER_PASSWORD);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenBadInfo_whenRegister_thenUnauthorizedResponse() {
        doThrow(RuntimeException.class).when(userService).register(BAD_USER_NAME, BAD_USER_PASSWORD);

        ResponseEntity<String> response = authenticationController.register(BAD_USER_NAME, BAD_USER_PASSWORD);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}