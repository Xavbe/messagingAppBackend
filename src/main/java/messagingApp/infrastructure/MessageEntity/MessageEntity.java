package messagingApp.infrastructure.MessageEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.User;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversationid", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public abstract void setContent(String content);
}