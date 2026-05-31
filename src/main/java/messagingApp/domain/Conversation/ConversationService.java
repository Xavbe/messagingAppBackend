package messagingApp.domain.Conversation;

import messagingApp.infrastructure.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    public List<Conversation> findByUsername (String username) {
        return conversationRepository.getAllConversationsforUsername(username);
    }
}
