package messagingApp.infrastructure.MessageEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
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
    private String sender;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public abstract void setContent(String content);
}