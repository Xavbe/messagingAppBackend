package messagingApp.controller.websocket;

import messagingApp.domain.message.MessageService;
import messagingApp.infrastructure.MessageEntity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageWebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/message.send")
    public void sendMessage(
            SendMessageRequest request,
            Principal principal) {

        MessageEntity message =
                messageService.sendMessage(
                        request.conversationId(),
                        principal.getName(),
                        request.content()
                );

        messagingTemplate.convertAndSend(
                "/topic/conversation/" +
                        request.conversationId(),
                message
        );
    }
}