package messagingApp.infrastructure;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Entity
public class Conversation {
    @Id
    private UUID id;

    @ElementCollection
    private ArrayList<String> usernames;

    private LocalDateTime lastMessageAt;

    public Conversation(ArrayList<String> usernames, LocalDateTime lastMessageAt) {
        id = UUID.randomUUID();
        this.usernames = usernames;
        this.lastMessageAt = LocalDateTime.now();
    }

    public Conversation() {
    }

    public void addMember(String username) {
        usernames.add(username);
    }

    public void removeMember(String username) {
        usernames.remove(username);
    }

    public ArrayList<String> getUsernames(){
        return usernames;
    }

    public LocalDateTime getLastMessageAt(){
        return lastMessageAt;
    }
    public void setLastMessageAt(LocalDateTime lastMessageAt){
        this.lastMessageAt = lastMessageAt;
    }

    public UUID getId(){
        return id;
    }
}
