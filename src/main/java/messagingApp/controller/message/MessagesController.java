package messagingApp.controller.message;

import messagingApp.domain.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversations/{conversationId/messages}")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @GetMapping
    public ResponseEntity<MessagesResponse> getMessages(@PathVariable("conversationId") String conversationId,
                                                              @RequestParam String messageBeforeUUID,
                                                              @RequestParam(defaultValue="30") int limit) {
        try {
            MessagesResponse response = messageMapper.getSendMessagesFormat(
                    messageService.getMessage(UUID.fromString(conversationId),
                            UUID.fromString(messageBeforeUUID), limit));
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
