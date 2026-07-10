package messagingApp.controller.authentication.me;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserbyId(userId);
        return ResponseEntity.ok(new MeResponse(user.getUsername(), user.getEmail()));
    }
}
