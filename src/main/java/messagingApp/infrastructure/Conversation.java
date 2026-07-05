package messagingApp.infrastructure;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Conversation {
    @Getter
    @Id
    private UUID id;
    private String name;

    @Getter
    @ManyToMany
    @JoinTable(
            name = "conversation_users",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @Getter
    private LocalDateTime lastMessageAt;

    public Conversation(String conversationName, ArrayList<User> users, LocalDateTime lastMessageAt) {
        id = UUID.randomUUID();
        this.users = users;
        this.lastMessageAt = lastMessageAt;
        this.name = conversationName;
    }

    public Conversation() {
    }

    public void addMember(User user) {
        users.add(user);
    }

    public void removeMember(User user) {
        users.remove(user);
    }

    public String getName(){
        return name;
    }

}
