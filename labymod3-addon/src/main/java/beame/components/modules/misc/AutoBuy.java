package beame.components.modules.misc;

import beame.Essence;

import beame.util.IMinecraft;
import events.Event;
import events.EventKey;
import events.impl.EventStartPriceParsing;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

public class AutoBuy extends Module implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d SUnCXQnD
    public AutoBuy() {
        super("AutoBuy", Category.Misc, true, "Меню для настройки автобая");
        addSettings(autobuyGuiBind, parser, parser2);
    }

    public final BindSetting autobuyGuiBind = new BindSetting("Открыть GUI", 344);
    public final BooleanSetting parser = new BooleanSetting("Автопарсер",false);
    public final SliderSetting parser2 = new SliderSetting("Уменьшать цены на", 20,1,99,0.5f).setVisible(() -> parser.get());
    public final BooleanSetting autoSell = new BooleanSetting("Авто-продажа", false);
    public final SliderSetting autoSellPercent = new SliderSetting("Увеличить цену на %", 20, 1, 100, 1f).setVisible(() -> autoSell.get());

    @Override
    public void event(Event event) {
        if (event instanceof EventStartPriceParsing) {
            Essence.getHandler().autoBuy.startPriceParsing();
        }
        if (event instanceof events.impl.EventStopPriceParsing) {
            Essence.getHandler().autoBuy.stopPriceParsing();
        }
        if (event instanceof EventKey eventKey) {
            if ((eventKey).key == autobuyGuiBind.get()) {
                mc.displayGuiScreen(Essence.getHandler().autoBuyGUI);
            }
        }
        if (Essence.getHandler().autoBuy.isEnabled()) {
            if (event instanceof events.impl.player.EventUpdate) {
                Essence.getHandler().autoBuy.onUpdate((events.impl.player.EventUpdate) event);
                Essence.getHandler().autoBuy.processBuy();
            } else if (event instanceof events.impl.packet.EventPacket) {
                events.impl.packet.EventPacket packet = (events.impl.packet.EventPacket) event;
                if (packet.getPacket() instanceof net.minecraft.network.play.server.SChatPacket chatPacket) {
                    String chatMessage = chatPacket.getChatComponent().getString();
                    event.setCancel(Essence.getHandler().autoBuy.onChatMessage(chatMessage));
                }
            } else if (event instanceof events.impl.player.EventContainerUpdated) {
                Essence.getHandler().autoBuy.processBuy();
            }
        }
        if (event instanceof events.impl.player.EventUpdate) {
            Essence.getHandler().autoBuy.up();
        }
        Essence.getHandler().autoBuy.priceParser.handle(event);
    }
}
