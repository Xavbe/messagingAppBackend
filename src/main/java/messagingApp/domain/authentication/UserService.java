package messagingApp.domain.authentication;

import lombok.RequiredArgsConstructor;
import messagingApp.controller.authentication.JwtAuthentificationSecurity;
import messagingApp.infrastructure.Status;
import messagingApp.infrastructure.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final JwtAuthentificationSecurity jwtAuthentificationSecurity;

    public String register(String username, String email, String password) {
        validateRegistrationRequest(username, email, password);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExists(username);
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExists(email);
        }

        User user = new User(username, email, BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setStatus(Status.ONLINE);
        User savedUser = userRepository.save(user);
        return jwtAuthentificationSecurity.generateToken(savedUser.getId());
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (! BCrypt.checkpw(password, user.getHashedPassword())) {
                throw new IncorrectPassword("Incorrect password");
        }

        user.setStatus(Status.ONLINE);
        userRepository.save(user);

        return jwtAuthentificationSecurity.generateToken(user.getId());
    }

    public void disconnect(String user) {
        var storedUser = userRepository.findByUsername(user.describeConstable().orElse(null));
        storedUser.ifPresent(value -> {
            value.setStatus(Status.OFFLINE);
            userRepository.save(value);
        });
    }

    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }

    public User findUserbyId(long senderId) {
        return userRepository.findById(senderId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void validateRegistrationRequest(String username, String email, String password) {
        if (isBlank(username) || isBlank(email) || isBlank(password)) {
            throw new IllegalArgumentException("Username, email and password are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
