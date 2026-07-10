package messagingApp.authentication;

import messagingApp.controller.authentication.JwtAuthentificationSecurity;
import messagingApp.domain.authentication.*;
import messagingApp.infrastructure.Status;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private static final String CORRECT_USERNAME = "patrice";
    private static final String CORRECT_EMAIL = "patrice@example.com";
    private static final String EXISTING_EMAIL = "existing@example.com";
    private static final String FRIEND_EMAIL = "friend@example.com";
    private static final Long CURRENT_USER_ID = 1L;
    private static final Long FRIEND_USER_ID = 2L;
    private static final String CORRECT_PASSWORD = "password*!";
    private static final String EXISTING_USERNAME = "patrice";

    private static final String WRONG_USERNAME = "john";
    private static final String WRONG_PASSWORD = "passsssword*!";

    @Mock
    private User EXISTING_USER;

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
        when(userRepository.findByEmail(CORRECT_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        when(jwtAuthentificationSecurity.generateToken(anyLong())).thenReturn("token");

        userService.register(CORRECT_USERNAME, CORRECT_EMAIL, CORRECT_PASSWORD);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void givenExistingUsername_whenRegister_thenUserAlreadyExistsError() {
        when(userRepository.findByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(EXISTING_USER));

        assertThrows(UserAlreadyExists.class, () -> userService.register(CORRECT_USERNAME, CORRECT_EMAIL,
                CORRECT_PASSWORD));
    }

    @Test
    void givenExistingEmail_whenRegister_thenUserAlreadyExistsError() {
        when(userRepository.findByUsername(CORRECT_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(EXISTING_EMAIL)).thenReturn(Optional.of(EXISTING_USER));

        assertThrows(UserAlreadyExists.class, () -> userService.register(CORRECT_USERNAME, EXISTING_EMAIL,
                CORRECT_PASSWORD));
    }

    @Test
    void givenExistingUserName_whenRegister_thenNoUserCreated() {
        when(userRepository.findByUsername(CORRECT_USERNAME)).thenReturn(Optional.of(EXISTING_USER));

        try {
            userService.register(CORRECT_USERNAME, CORRECT_EMAIL, CORRECT_PASSWORD);
        } catch (UserAlreadyExists ignored) {}

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
        when(jwtAuthentificationSecurity.generateToken(anyLong())).thenReturn("token");

        assertDoesNotThrow(() -> userService.login(CORRECT_USERNAME, CORRECT_PASSWORD));
    }

    @Test
    void givenExistingUser_whenDisconnect_thenStatusSetToOffline() {
        when(userRepository.findByUsername(CORRECT_USERNAME)).thenReturn(Optional.of(EXISTING_USER));

        userService.disconnect(CORRECT_USERNAME);

        verify(EXISTING_USER).setStatus(Status.OFFLINE);
        verify(userRepository).save(EXISTING_USER);
    }

    @Test
    void givenNonExistingUser_whenDisconnect_thenNoStatusChange() {
        when(userRepository.findByUsername(WRONG_USERNAME)).thenReturn(Optional.empty());

        userService.disconnect(WRONG_USERNAME);

        verify(EXISTING_USER, never()).setStatus(any());
    }

    @Test
    void givenConnectedUsers_whenFindConnectedUsers_thenReturnOnlineUsers() {
        List<User> onlineUsers = List.of(EXISTING_USER);
        when(userRepository.findAllByStatus(Status.ONLINE)).thenReturn(onlineUsers);

        List<User> result = userService.findConnectedUsers();

        assertEquals(onlineUsers, result);
        verify(userRepository).findAllByStatus(Status.ONLINE);
    }

    @Test
    void givenNoConnectedUsers_whenFindConnectedUsers_thenReturnEmptyList() {
        when(userRepository.findAllByStatus(Status.ONLINE)).thenReturn(List.of());

        List<User> result = userService.findConnectedUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void givenExistingUsers_whenAddFriend_thenFriendIsAddedToCurrentUser() {
        User currentUser = new User(CORRECT_USERNAME, CORRECT_EMAIL, "hash");
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setFriends(new ArrayList<>());
        User friend = new User("friend", FRIEND_EMAIL, "hash");
        friend.setId(FRIEND_USER_ID);

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(FRIEND_EMAIL)).thenReturn(Optional.of(friend));

        userService.addFriend(CURRENT_USER_ID, FRIEND_EMAIL);

        assertTrue(currentUser.getFriends().contains(friend));
    }

    @Test
    void givenUnknownCurrentUser_whenAddFriend_thenThrowsException() {
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.addFriend(CURRENT_USER_ID, FRIEND_EMAIL));
    }

    @Test
    void givenExistingUsers_whenAddFriend_thenCurrentUserIsSaved() {
        User currentUser = new User(CORRECT_USERNAME, CORRECT_EMAIL, "hash");
        currentUser.setId(CURRENT_USER_ID);
        User friend = new User("friend", FRIEND_EMAIL, "hash");
        friend.setId(FRIEND_USER_ID);

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(FRIEND_EMAIL)).thenReturn(Optional.of(friend));

        userService.addFriend(CURRENT_USER_ID, FRIEND_EMAIL);

        verify(userRepository).save(currentUser);
    }

    @Test
    void givenSameUser_whenAddFriend_thenThrowsException() {
        User currentUser = new User(CORRECT_USERNAME, CORRECT_EMAIL, "hash");
        currentUser.setId(CURRENT_USER_ID);
        User sameUser = new User(CORRECT_USERNAME, CORRECT_EMAIL, "hash");
        sameUser.setId(CURRENT_USER_ID);

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(CORRECT_EMAIL)).thenReturn(Optional.of(sameUser));

        assertThrows(IllegalArgumentException.class, () -> userService.addFriend(CURRENT_USER_ID, CORRECT_EMAIL));
    }

    @Test
    void givenKnownUser_whenGetFriends_thenReturnsFriends() {
        User friend = new User("friend", FRIEND_EMAIL, "hash");
        User currentUser = new User(CORRECT_USERNAME, CORRECT_EMAIL, "hash");
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setFriends(List.of(friend));

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(currentUser));

        List<User> result = userService.getFriends(CURRENT_USER_ID);

        assertEquals(List.of(friend), result);
    }

    @Test
    void givenUnknownUser_whenGetFriends_thenThrowsException() {
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getFriends(CURRENT_USER_ID));
    }
}
