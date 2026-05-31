package messagingApp.infrastructure.MessageEntity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "message")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class MessageEntity {

    @Id
    private UUID id;

    @Column(name = "conversationid", nullable=false)
    private UUID conversationId;

    @Column(name = "senderid", nullable=false)
    private UUID senderId;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public UUID getId() {
        return id;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public LocalDateTime getTimeStamp() {
        return timestamp;
    }

}