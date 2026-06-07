package messagingApp.domain.authentication;

import messagingApp.infrastructure.Status;
import messagingApp.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findAllByStatus(Status status);
}
