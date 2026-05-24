package messagingApp.controller.message;

import messagingApp.domain.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageMapper {
    public MessagesResponse getSendMessagesFormat(List<Message> messages) {
        List<MessageResponse> messagesResponse = new ArrayList<>();
        for (Message message1 : messages) {
            messagesResponse.add(getMessageToSendFormat(message1));
        }

        return new MessagesResponse(messagesResponse, getLastMessagesOfConversation(messages),
                getLastMessagesId(messages));
    }

    private MessageResponse getMessageToSendFormat(Message message) {
        return new MessageResponse (message.getMessageId(), message.getContent(), getType(message),
                message.getSenderId(),
                message.getTimestamp());
    }

    private String getType(Message message) {
        if (message instanceof Message) {
            return "TEXT";
        }
        throw new IllegalArgumentException("Message type not supported");
    }

    private String getLastMessagesId(List<Message> messages) {
        return  messages.getLast().getMessageId().toString();
    }

    private String getLastMessagesOfConversation(List<Message> messages){
        if (messages == null || messages.size() < 30) {
            return "TRUE";
        } else  {
            return "FALSE";
        }
    }
}
