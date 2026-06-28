package messagingApp.domain.Conversation;

import messagingApp.infrastructure.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    public List<Conversation> findByUsername (String username) {
        return conversationRepository.getAllConversationsforUsername(username);
    }

    public Conversation getConversationById(UUID conversationId) {
        return conversationRepository.findById(conversationId).orElseThrow(() ->
                        new ConversationNotFoundException(conversationId));
    }
}
