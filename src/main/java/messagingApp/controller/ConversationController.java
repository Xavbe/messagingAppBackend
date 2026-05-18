package messagingApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import messagingApp.infrastructure.Conversation;
import messagingApp.domain.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getConversations(HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(
                conversationService.findByUsername((String) request.getSession().getAttribute("user")));
    }

}
