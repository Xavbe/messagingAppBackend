package infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String hashedPassword;

    public User(String correctUsername, String correctPassword) {
        this.username = correctUsername;
        this.hashedPassword = correctPassword;
    }

    public User() {
    }

    public String getUsername(){
        return username;
    }

    public String getHashedPassword(){
        return hashedPassword;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setHashedPassword(String hashedPassword){
        this.hashedPassword = hashedPassword;
    }

}