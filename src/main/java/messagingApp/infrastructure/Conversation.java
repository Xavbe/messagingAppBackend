package messagingApp.infrastructure;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Entity
public class Conversation {
    @Id
    private UUID id;

    @ManyToMany
    @JoinTable(
            name = "conversation_users",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private ArrayList<User> users = new ArrayList<>();

    private LocalDateTime lastMessageAt;

    public Conversation(ArrayList<User> users, LocalDateTime lastMessageAt) {
        id = UUID.randomUUID();
        this.users = users;
        this.lastMessageAt = lastMessageAt;
    }

    public Conversation() {
    }

    public void addMember(User user) {
        users.add(user);
    }

    public void removeMember(User user) {
        users.remove(user);
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public LocalDateTime getLastMessageAt(){
        return lastMessageAt;
    }

    public UUID getId(){
        return id;
    }
}
