package messagingApp.domain.Conversation;

import messagingApp.infrastructure.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c WHERE :username MEMBER OF c.users ORDER BY c.lastMessageAt DESC")
    List<Conversation> getAllConversationsforUsername(@Param("username") String username);
}
