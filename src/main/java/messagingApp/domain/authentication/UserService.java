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

    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExists(username);
        }

        User user = new User(username, BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
        return jwtAuthentificationSecurity.generateToken(username);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (! BCrypt.checkpw(password, user.getHashedPassword())) {
                throw new IncorrectPassword("Incorrect password");
        }

        user.setStatus(Status.ONLINE);

        return jwtAuthentificationSecurity.generateToken(username);
    }

    public void disconnect(String user) {
        var storedUser = userRepository.findByUsername(user.describeConstable().orElse(null));
        storedUser.ifPresent(value -> value.setStatus(Status.OFFLINE));
    }

    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }
}
