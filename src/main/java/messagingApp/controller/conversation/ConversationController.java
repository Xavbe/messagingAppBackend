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

    @Autowired
    private UserService userService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String username = userService.findUserbyId(userId).getUsername();
        List<ConversationResponse> conversations = conversationService.findByUsername(username)
                .stream()
                .map(ConversationResponse::from)
                .toList();
        return ResponseEntity.ok(conversations);

    }

    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation(
            @RequestBody CreateConversationRequest body,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String currentUsername = userService.findUserbyId(userId).getUsername();

        Conversation conversation = conversationService.createConversation(
                body.conversationName(),
                currentUsername,
                body.usernames()
        );

        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }
}