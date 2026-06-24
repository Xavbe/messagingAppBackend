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
    public void sendMessage(SendMessageRequest request, Principal principal) {

        Long userId = getUserId(principal);

        MessageEntity message = messageService.sendMessage(
                request.conversationId(),
                userId,
                request.content()
        );

        messagingTemplate.convertAndSend(
                "/topic/conversations/" + request.conversationId() + "/messages",
                message
        );
    }

    private Long getUserId(Principal principal) {
        if (principal instanceof CustomUserPrincipal customUserPrincipal) {
            return customUserPrincipal.getUserId();
        }

        return Long.parseLong(principal.getName());
    }
}
