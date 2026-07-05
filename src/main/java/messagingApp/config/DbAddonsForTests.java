package messagingApp.config;

import messagingApp.domain.Conversation.ConversationService;
import messagingApp.domain.authentication.UserRepository;
import messagingApp.domain.authentication.UserService;
import messagingApp.domain.message.MessageService;
import messagingApp.infrastructure.Conversation;
import messagingApp.infrastructure.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class DbAddonsForTests {

    @Bean
    @Profile("dev")
    public CommandLineRunner createTestUser(UserRepository userRepository, UserService userService,
                                            ConversationService conversationService, MessageService messageService) {
        return args -> {
            if (userRepository.findByUsername("test").isEmpty()) {
                userService.register("test", "test@example.com", "password123");
                userService.register("xavier", "allo@json.com", "test123");
                Conversation conversation = conversationService.createConversation("Patate!",
                        "xavier", List.of("test"));

                messageService.sendMessage(conversation.getId(),
                        userService.findByUsername("xavier").getId(), "Hi How are you!" );
                messageService.sendMessage(conversation.getId(),
                        userService.findByUsername("test").getId(), "Well and you !" );

                System.out.println("User and conversations created !");
            }
        };
    }
}