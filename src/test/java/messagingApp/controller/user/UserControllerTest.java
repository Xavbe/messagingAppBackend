package messagingApp.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.domain.authentication.UserService;
import messagingApp.domain.authentication.UsernameNotFoundException;
import messagingApp.infrastructure.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String CURRENT_USER_EMAIL = "alice@example.com";
    private static final String FRIEND_USER_EMAIL = "bob@example.com";
    private static final Long CURRENT_USER_ID = 1L;

    @Mock
    private UserService userService;

    @Mock
    private FriendMapper friendMapper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserController userController;

    @Test
    void givenAuthenticatedUser_whenAddFriend_thenReturnsOk() {
        when(request.getAttribute("userId")).thenReturn(CURRENT_USER_ID);

        ResponseEntity<?> response =
                userController.addFriend(new AddFriendRequest(FRIEND_USER_EMAIL), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenAuthenticatedUser_whenAddFriend_thenServiceCalled() {
        when(request.getAttribute("userId")).thenReturn(CURRENT_USER_ID);

        userController.addFriend(new AddFriendRequest(FRIEND_USER_EMAIL), request);

        verify(userService).addFriend(CURRENT_USER_ID, FRIEND_USER_EMAIL);
    }

    @Test
    void givenNoAuthenticatedUser_whenAddFriend_thenReturnsUnauthorized() {
        when(request.getAttribute("userId")).thenReturn(null);

        ResponseEntity<?> response =
                userController.addFriend(new AddFriendRequest(FRIEND_USER_EMAIL), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void givenServiceError_whenAddFriend_thenReturnsBadRequest() {
        when(request.getAttribute("userId")).thenReturn(CURRENT_USER_ID);
        doThrow(new UsernameNotFoundException("User not found"))
                .when(userService)
                .addFriend(CURRENT_USER_ID, FRIEND_USER_EMAIL);

        ResponseEntity<?> response =
                userController.addFriend(new AddFriendRequest(FRIEND_USER_EMAIL), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenAuthenticatedUser_whenGetFriends_thenReturnsMappedFriends() {
        User friend = createUser("bob", FRIEND_USER_EMAIL);
        ResponseFriendList mappedFriends =
                new ResponseFriendList(List.of(new ResponseFriend("bob", FRIEND_USER_EMAIL)));

        when(request.getAttribute("userId")).thenReturn(CURRENT_USER_ID);
        when(userService.getFriends(CURRENT_USER_ID)).thenReturn(List.of(friend));
        when(friendMapper.getFriendList(List.of(friend))).thenReturn(mappedFriends);

        ResponseEntity<?> response = userController.getFriends(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mappedFriends);
    }

    @Test
    void givenAuthenticatedUser_whenGetFriends_thenServiceCalled() {
        when(request.getAttribute("userId")).thenReturn(CURRENT_USER_ID);
        when(userService.getFriends(CURRENT_USER_ID)).thenReturn(List.of());
        when(friendMapper.getFriendList(List.of())).thenReturn(new ResponseFriendList(List.of()));

        userController.getFriends(request);

        verify(userService).getFriends(CURRENT_USER_ID);
    }

    @Test
    void givenServiceError_whenGetFriends_thenReturnsBadRequest() {
        when(request.getAttribute("userId")).thenReturn(CURRENT_USER_ID);
        doThrow(new UsernameNotFoundException("User not found"))
                .when(userService)
                .getFriends(CURRENT_USER_ID);

        ResponseEntity<?> response = userController.getFriends(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenNoAuthenticatedUser_whenGetFriends_thenReturnsUnauthorized() {
        when(request.getAttribute("userId")).thenReturn(null);

        ResponseEntity<?> response = userController.getFriends(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
