package messagingApp.infrastructure.MessageEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TEXT")
public class TextMessageEntity extends MessageEntity {

    @Column(name = "content", nullable = false)
    private String content;

    public String getContent(){
        return content;
    }
}