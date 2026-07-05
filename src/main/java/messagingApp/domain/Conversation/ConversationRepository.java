package messagingApp.domain.Conversation;

import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT c FROM Conversation c JOIN c.users u WHERE u.id = :userId")
    List<Conversation> getAllConversationsForUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c JOIN c.users u WHERE c.name = :name AND u.id = :userId")
    Optional<Conversation> getConversationByNameAndContainingUser(@Param("name") String name, @Param("userId") Long userId);
}
