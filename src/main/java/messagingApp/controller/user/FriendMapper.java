package messagingApp.controller.user;
import messagingApp.infrastructure.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FriendMapper {

    public ResponseFriendList getFriendList(List<User> responseList){
        List<ResponseFriend> friends = responseList.stream()
                .map(user -> new ResponseFriend(user.getUsername(), user.getEmail()))
                .toList();

        return new ResponseFriendList(friends);
    }
}
