package messagingApp.controller;

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
    public ResponseEntity<String> login(@RequestBody AuthenticationRequest request) {
        try{
            userService.login(request.username(), request.password());
            return ResponseEntity.ok("User connected");
        } catch (Exception e){
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthenticationRequest request) {
        try{
            userService.register(request.username(), request.password());
            return ResponseEntity.ok("User created");
        } catch (UserAlreadyExists e){
            return new ResponseEntity<>("Username already exists", HttpStatus.UNAUTHORIZED);
        }
    }

}
