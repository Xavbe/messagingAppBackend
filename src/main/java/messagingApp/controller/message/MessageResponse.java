package messagingApp.controller.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(UUID id, String message, String type, String sender, LocalDateTime timestamp) {
}
