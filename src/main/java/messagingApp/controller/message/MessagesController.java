package messagingApp.controller.message;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/conversations/{conversationId}/messages")
public class MessagesController {

    private MessageService messageService;
    private MessageMapper messageMapper;


    private ConversationService conversationService;

    public MessagesController(MessageService messageService, MessageMapper messageMapper, ConversationService conversationService) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<MessagesResponse> getMessages(
            @PathVariable("conversationId") String conversationId,
            @RequestParam(required = false) String messageBeforeUUID,
            @RequestParam(defaultValue = "30") int limit,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UUID convId = UUID.fromString(conversationId);

        if (!conversationService.isMember(convId, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            UUID before = messageBeforeUUID != null ? UUID.fromString(messageBeforeUUID) : null;
            MessagesResponse response = messageMapper.getSendMessagesFormat(
                    messageService.getMessage(convId, before, limit));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
