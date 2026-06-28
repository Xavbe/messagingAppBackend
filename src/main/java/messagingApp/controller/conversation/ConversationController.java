package messagingApp.controller.conversation;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.Conversation;
import messagingApp.domain.Conversation.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getConversations(HttpServletRequest request) {
        if (getCurrentUsername(request) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(
                conversationService.findByUsername(getCurrentUsername(request)));
    }

    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation( @RequestBody CreateConversationRequest body,
                                                            HttpServletRequest request) {
        if (getCurrentUsername(request) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Conversation conversation = conversationService.createConversation(body.conversationName(),
                getCurrentUsername(request), body.usernames());
        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }


    private String getCurrentUsername(HttpServletRequest request) {
        try {
            return request.getSession().getAttribute("user").toString();

        } catch (NullPointerException e) {
            return null;
        }
    }
}
