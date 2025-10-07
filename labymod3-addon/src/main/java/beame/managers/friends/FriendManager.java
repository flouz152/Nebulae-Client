package beame.managers.friends;

import beame.components.command.AbstractCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendManager {
// leaked by itskekoff; discord.gg/sk3d 8Ghw7vIM
    public FriendManager() {}

    private List<Friend> friends = new ArrayList<>();

    public boolean isFriend(String username) {
        for(Friend friend : friends){
            if(Objects.equals(friend.username, username))
                return true;
        }
        return false;
    }
    
    public List<String> getFriendNames() {
        return friends.stream()
                .map(friend -> friend.username)
                .collect(Collectors.toList());
    }
    
    public void addFriend(String username) {
        if(isFriend(username))
            return;

        friends.add(new Friend(username));
        AbstractCommand.addMessage(username + " добавлен в друзья");
    }

    public void remFriend(String username) {
        if(!isFriend(username))
            return;

        friends.removeIf(friend -> Objects.equals(friend.username, username));
        AbstractCommand.addMessage(username + " удалён из друзей");
    }

    private class Friend {
        public String username;
        public Friend(String username) {
            this.username = username;
        }
    }
}
