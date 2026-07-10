package messagingApp.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.domain.authentication.UserService;
import messagingApp.domain.authentication.UsernameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FriendMapper friendMapper;

    @PostMapping("/friend")
    public ResponseEntity<?> addFriend(@RequestBody AddFriendRequest body, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            userService.addFriend(currentUserId, body.friendEmail());
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/friend")
    public ResponseEntity<?> getFriends(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            ResponseFriendList responseFriendList =
                    friendMapper.getFriendList(userService.getFriends(currentUserId));
            return ResponseEntity.ok(responseFriendList);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
