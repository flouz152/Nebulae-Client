package beame.components.modules.misc;

import beame.Essence;
import events.Event;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.server.SChatPacket;
import beame.setting.SettingList.BooleanSetting;

public class AutoAccept extends Module {
// leaked by itskekoff; discord.gg/sk3d NSvJ8piH
    public AutoAccept() {
        super("AutoAccept", Category.Misc, true, "Авто-принятие тп/завки в клан");
        addSettings(tpAccept, clanAccept, onlyFriend);
    }

    public final BooleanSetting tpAccept = new BooleanSetting("Принимать Телепорт", true, 0);
    public final BooleanSetting clanAccept = new BooleanSetting("Принимать Заявку в клан", true, 0);
    public final BooleanSetting onlyFriend = new BooleanSetting("Только от друзей", true, 0);

    @Override
    public void event(Event event) {
        if(event instanceof EventPacket){
            if (((EventPacket) event).getPacket() instanceof SChatPacket chatPacket) {
                String chatMessage = chatPacket.getChatComponent().getString();

                
                if(chatMessage.contains(" хочет телепортироваться к вам.")) {

                    String[] parts = chatMessage.split(" хочет телепортироваться к вам.");
                    if (parts.length > 0) {
                        String beforeRequest = parts[0];
                        String[] words = beforeRequest.trim().split(" ");
                        String sender = words[words.length - 1];

                        
                        boolean needAccept = onlyFriend.get() ? Essence.getHandler().friends.isFriend(sender) : true;

                        if(!tpAccept.get()) {
                            return;
                        }
                        if(!needAccept) {
                            return;
                        }

                        mc.player.sendChatMessage("/tpaccept");
                    }
                    
                } else if(chatMessage.contains(" приглашает Вас в клан ")) {

                    String[] parts = chatMessage.split(" приглашает Вас в клан ");
                    if (parts.length > 0) {
                        String beforeRequest = parts[0];
                        String[] words = beforeRequest.trim().split(" ");
                        String sender = words[words.length - 1];
                        

                        
                        boolean needAccept = onlyFriend.get() ? Essence.getHandler().friends.isFriend(sender) : true;

                        if(!clanAccept.get()) {
                            return;
                        }
                        if(!needAccept) {
                            return;
                        }

                        mc.player.sendChatMessage("/clan accept " + sender);
                    }
                }
            }
        }
    }
}