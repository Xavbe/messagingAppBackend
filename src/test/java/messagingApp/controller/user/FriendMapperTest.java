package messagingApp.controller.user;

import messagingApp.infrastructure.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FriendMapperTest {

    private final FriendMapper friendMapper = new FriendMapper();

    @Test
    void givenUsers_whenGetFriendList_thenMapsNameAndEmail() {
        User alice = createUser("alice", "alice@example.com");
        User bob = createUser("bob", "bob@example.com");

        ResponseFriendList response = friendMapper.getFriendList(List.of(alice, bob));

        assertThat(response.friends())
                .containsExactly(
                        new ResponseFriend("alice", "alice@example.com"),
                        new ResponseFriend("bob", "bob@example.com")
                );
    }

    @Test
    void givenNoUsers_whenGetFriendList_thenReturnsEmptyList() {
        ResponseFriendList response = friendMapper.getFriendList(List.of());

        assertThat(response.friends()).isEmpty();
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}
