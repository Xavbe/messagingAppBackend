package messagingApp.domain.message;

import messagingApp.infrastructure.MessageEntity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

//For now the repository is in PostgreSQL but maybe use Cassandra like Big Tech
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Query("""
        SELECT m FROM MessageEntity m
        WHERE m.conversationId = :conversationId
          AND m.timestamp < (
              SELECT m2.timestamp FROM MessageEntity m2 WHERE m2.id = :beforeId
          )
        ORDER BY m.timestamp DESC
        LIMIT :limit
    """)
    List<MessageEntity> findMessagesBefore(
            @Param("conversationId") UUID conversationId,
            @Param("beforeId") UUID beforeId,
            @Param("limit") int limit
    );
}
