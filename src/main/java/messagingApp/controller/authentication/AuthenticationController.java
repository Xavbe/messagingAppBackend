package messagingApp.controller.authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import messagingApp.domain.authentication.UserAlreadyExists;
import messagingApp.domain.authentication.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @PostMapping("/login")
    @SendTo("/user/topic")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        try {
            String token = userService.login(request.username(), request.password());
            setCookie(token, response);
            return ResponseEntity.ok("User connected");
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    @SendTo("/user/topic")
    public ResponseEntity<?> register(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        try {
            String token = userService.register(request.username(), request.email(), request.password());
            setCookie(token, response);
            return ResponseEntity.ok("User created");
        } catch (UserAlreadyExists e) {
            return new ResponseEntity<>("User already exists", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/disconnect")
    @SendTo("/user/topic")
    public ResponseEntity<?> disconnect(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        userService.disconnect(request.username());
        return ResponseEntity.ok("User created");
    }

    private void setCookie(String token, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("session", token)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
