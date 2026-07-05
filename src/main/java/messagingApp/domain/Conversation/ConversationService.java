package messagingApp.domain.Conversation;

import messagingApp.domain.authentication.UserService;
import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserService userService;

    public List<Conversation> findByUsername (String username) {
        User user = userService.findByUsername(username);
        return conversationRepository.getAllConversationsForUserId(user.getId());
    }

    public Conversation getConversationById(UUID conversationId) {
        return conversationRepository.findById(conversationId).orElseThrow(() ->
                        new ConversationNotFoundException(conversationId));
    }


    public Conversation createConversation(String ConversationName, String currentUsername, List<String> usernames) {
        ArrayList<User> participants = new ArrayList<>();

        User creator = userService.findByUsername(currentUsername);
        participants.add(creator);

        for (String username : usernames) {
            User user = userService.findByUsername(username);
            if (!participants.contains(user)) {
                participants.add(user);
            }
        }

        Conversation conversation = new Conversation(ConversationName, participants, LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    public Conversation getConversationWithUserAndName(long userId, String conversationName) {
        return conversationRepository.getConversationByNameAndContainingUser(conversationName, userId)
                .orElseThrow(() -> new ConversationNotFoundException(conversationName));
    }

    public boolean isMember(UUID conversationId, Long userId) {
        Conversation conversation = getConversationById(conversationId);
        return conversation.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));
    }
}
