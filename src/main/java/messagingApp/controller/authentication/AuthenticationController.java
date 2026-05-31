package messagingApp.controller.authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import messagingApp.domain.authentication.UserAlreadyExists;
import messagingApp.domain.authentication.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        try{
            String token = userService.login(request.username(), request.password());
            setCookie(token, response);
            return ResponseEntity.ok("User connected");
        } catch (Exception e){
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        try{
            String token = userService.register(request.username(), request.password());
            setCookie(token, response);
            return ResponseEntity.ok("User created");
        } catch (UserAlreadyExists e){
            return new ResponseEntity<>("Username already exists", HttpStatus.UNAUTHORIZED);
        }
    }

    private void setCookie(String token, HttpServletResponse response){
        Cookie cookie = new Cookie("session", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // passer à true en production
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }
}
