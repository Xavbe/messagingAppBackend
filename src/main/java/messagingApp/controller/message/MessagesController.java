package messagingApp.controller.message;

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

    public MessagesController(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @GetMapping
    public ResponseEntity<MessagesResponse> getMessages(
            @PathVariable("conversationId") String conversationId,
            @RequestParam(required = false) String messageBeforeUUID,
            @RequestParam(defaultValue = "30") int limit) {
        try {
            UUID beforeId = messageBeforeUUID != null ? UUID.fromString(messageBeforeUUID) : null;

            MessagesResponse response = messageMapper.getSendMessagesFormat(
                    messageService.getMessage(UUID.fromString(conversationId), beforeId, limit));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
