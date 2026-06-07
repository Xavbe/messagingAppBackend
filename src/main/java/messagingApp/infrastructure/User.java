package messagingApp.infrastructure;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String hashedPassword;

    @Column(nullable = false)
    private Status status;

    public User(String correctUsername, String correctPassword) {
        this.username = correctUsername;
        this.hashedPassword = correctPassword;
    }

    public User() {
    }
}