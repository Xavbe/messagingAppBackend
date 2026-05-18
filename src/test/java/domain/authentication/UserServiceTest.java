package domain.authentication;

import messagingApp.controller.authentication.JwtAuthentificationSecurity;
import messagingApp.domain.authentication.*;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private static final String CORRECT_USERNAME = "patrice";
    private static final String CORRECT_PASSWORD = "password*!";
    private static final String EXISTING_USERNAME = "patrice";

    private static final String WRONG_USERNAME = "john";
    private static final String WRONG_PASSWORD = "passsssword*!";

    @Mock
    private User EXISTING_USER =  new User();

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtAuthentificationSecurity jwtAuthentificationSecurity;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void initUserServiceMock() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenUnexistingUserName_whenRegister_thenUserCreated() {
        when(userRepository.findByUsername(CORRECT_USERNAME)).thenReturn(Optional.empty());
        when(jwtAuthentificationSecurity.generateToken(anyString())).thenReturn("token");


        userService.register(CORRECT_USERNAME, CORRECT_PASSWORD);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void givenExistingUsername_whenRegister_thenUserAlreadyExistsError() {
        when(userRepository.findByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(EXISTING_USER));

        assertThrows(UserAlreadyExists.class, () -> userService.register(CORRECT_USERNAME, CORRECT_PASSWORD));
    }

    @Test
    void givenExistingUserName_whenRegister_thenNoUserCreated() {
        when(userRepository.findByUsername(CORRECT_USERNAME)).thenReturn(Optional.of(EXISTING_USER));

        try { userService.register(CORRECT_USERNAME, CORRECT_PASSWORD); } catch (UserAlreadyExists ignored) {}

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenUnexistingUsername_whenLogin_thenThrowUsernameNotFoundException() {
        when(userRepository.findByUsername(WRONG_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.login(WRONG_USERNAME, CORRECT_PASSWORD));
    }

    @Test
    void givenWrongPassword_whenLogin_thenThrowIncorrectPassword() {
        when(userRepository.findByUsername(CORRECT_USERNAME)).thenReturn(Optional.of(EXISTING_USER));
        when(EXISTING_USER.getHashedPassword()).thenReturn(BCrypt.hashpw(CORRECT_PASSWORD, BCrypt.gensalt()));

        assertThrows(IncorrectPassword.class, () -> userService.login(CORRECT_USERNAME, WRONG_PASSWORD));
    }

    @Test
    void givenCorrectPassword_whenLogin_thenSuccess() {
        when(userRepository.findByUsername(CORRECT_USERNAME)).thenReturn(Optional.of(EXISTING_USER));
        when(EXISTING_USER.getHashedPassword()).thenReturn(BCrypt.hashpw(CORRECT_PASSWORD, BCrypt.gensalt()));
        when(jwtAuthentificationSecurity.generateToken(anyString())).thenReturn("token");

        assertDoesNotThrow(() -> userService.login(CORRECT_USERNAME, CORRECT_PASSWORD));
    }

}