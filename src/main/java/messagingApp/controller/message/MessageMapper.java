package messagingApp.controller.message;

import messagingApp.infrastructure.MessageEntity.MessageEntity;
import messagingApp.infrastructure.MessageEntity.TextMessageEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageMapper {
    public MessagesResponse getSendMessagesFormat(List<MessageEntity> messages) {
        List<MessageResponse> messagesResponse = new ArrayList<>();
        for (MessageEntity message1 : messages) {
            messagesResponse.add(getMessageToSendFormat(message1));
        }

        return new MessagesResponse(messagesResponse, getLastMessagesOfConversation(messages),
                getLastMessagesId(messages));
    }

    private MessageResponse getMessageToSendFormat(MessageEntity message) {
        return new MessageResponse (message.getId(),getContent(message), getType(message),
                message.getSender(),
                message.getTimestamp());
    }

    private String getType(MessageEntity message) {
        if (message instanceof TextMessageEntity) {
            return "TEXT";
        }
        throw new IllegalArgumentException("Message type not supported");
    }

    private String getLastMessagesId(List<MessageEntity> messages) {
        return  messages.getLast().getId().toString();
    }

    private String getContent(MessageEntity message) {
        if (message instanceof TextMessageEntity){
            return ((TextMessageEntity)message).getContent();
        }
        throw new IllegalArgumentException("Message type not supported");
    }

    private String getLastMessagesOfConversation(List<MessageEntity> messages){
        if (messages == null || messages.size() < 30) {
            return "TRUE";
        } else  {
            return "FALSE";
        }
    }
}
