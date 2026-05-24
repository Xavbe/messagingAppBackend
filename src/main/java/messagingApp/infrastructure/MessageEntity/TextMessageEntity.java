package messagingApp.infrastructure.MessageEntity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TEXT")
public class TextMessageEntity extends MessageEntity {
    private String content;
}