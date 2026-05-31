package messagingApp.domain.authentication;

import messagingApp.controller.authentication.JwtAuthentificationSecurity;
import messagingApp.infrastructure.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtAuthentificationSecurity jwtAuthentificationSecurity;

    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExists(username);
        }

        User user = new User(username, BCrypt.hashpw(password, BCrypt.gensalt()));
        userRepository.save(user);
        return jwtAuthentificationSecurity.generateToken(username);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (! BCrypt.checkpw(password, user.getHashedPassword())) {
                throw new IncorrectPassword("Incorrect password");
        }

        return jwtAuthentificationSecurity.generateToken(username);
    }
}
