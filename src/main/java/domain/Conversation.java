package domain;

import java.util.ArrayList;
import java.util.UUID;

public class Conversation {
    private UUID id;
    private ArrayList<UUID> usersId;

    public Conversation() {
        id = UUID.randomUUID();
        usersId = new ArrayList<>();
    }

    public void addMember(UUID newMemberUUID){
        usersId.add(newMemberUUID);
    }

    public void removeMember(UUID newMemberUUID){
        usersId.remove(newMemberUUID);
    }

    public ArrayList<UUID> getUsersId(){
        return usersId;
    }
}
