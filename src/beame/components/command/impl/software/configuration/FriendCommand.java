package beame.components.command.impl.software.configuration;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.util.other.SoundUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@AbstractCommand.CommandInfo(name = "friend", description = "Друзья")
public class FriendCommand extends AbstractCommand {
// leaked by itskekoff; discord.gg/sk3d 6NbctXEx

    @Override
    public void run(String[] args) {
        if (args.length < 3) {
            error();
            return;
        }

        String action = args[1].toLowerCase();
        String nickname = args[2];

        Map<String, Consumer<String>> actions = Map.of(
                "add", this::addFriend,
                "remove", this::removeFriend
        );

        if (!actions.containsKey(action)) {
            error();
            return;
        }

        actions.get(action).accept(nickname);
        addMessage("Успех!");
    }

    private void addFriend(String name) {
        Nebulae.getHandler().friends.addFriend(name);
        var clientSounds = Nebulae.getHandler().getModuleList().getClientSounds();
        if (clientSounds.isState() && clientSounds.soundActive.get(6).get()) {
            SoundUtil.playSound("friend.wav", clientSounds.volume.get(), false);
        }
    }

    private void removeFriend(String name) {
        Nebulae.getHandler().friends.remFriend(name);
    }

    @Override
    public void error() {
        printMessage(List.of(
                "[Ошибка] Используйте:",
                ".friend add <nickname>",
                ".friend remove <nickname>"
        ));
    }

    @Override
    public String name() {
        return "friend";
    }

    @Override
    public String description() {
        return "Друзья";
    }

    @Override
    public List<String> adviceMessage() {
        return List.of(
                ".friend add <nickname>",
                ".friend remove <nickname>"
        );
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }
}
