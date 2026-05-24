package messagingApp.infrastructure.MessageEntity;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "message")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // ou JOINED
@DiscriminatorColumn(name = "type")
public abstract class MessageEntity {

    @Id
    private UUID id;

}