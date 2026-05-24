package messagingApp.domain;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository {
    public List<Message> getMessages(UUID conversationId, UUID messageBeforeUUID, int limit);
}
