package messagingApp.controller.websocket;

import java.security.Principal;

public class CustomUserPrincipal implements Principal {

    private final Long userId;

    public CustomUserPrincipal(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return userId.toString();
    }
}
