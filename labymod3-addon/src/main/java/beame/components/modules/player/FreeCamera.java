package beame.components.modules.player;

import beame.util.fonts.Fonts;
import beame.util.other.Mathf;
import beame.util.other.MoveUtil;
import com.mojang.authlib.GameProfile;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventLivingUpdate;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.world.GameType;
import beame.setting.SettingList.SliderSetting;
import beame.setting.SettingList.BooleanSetting;

import java.util.UUID;

public class FreeCamera extends Module {
// leaked by itskekoff; discord.gg/sk3d Vqh9Pq1T
    private final SliderSetting speed = new SliderSetting("Скорость", 1.5F, 0.1F, 5.0F, 0.1F);
    private final BooleanSetting coordinates = new BooleanSetting("Координаты", true);
    private final int TEMP_ENTITY_ID = Integer.MAX_VALUE - 1337;
    private float x, y, z;
    private GameType prev;

    public FreeCamera() {
        super("FreeCamera", Category.Player, true, "Включает свободную камеру по миру, замораживая пакет сервера");
        addSettings(speed, coordinates);
    }

    @Override
    public void onEnable() {
        if (mc.player.getRidingEntity() != null) toggle();

        super.onEnable();
        prev = mc.playerController.getCurrentGameType();

        mc.player.connection.getPlayerInfo(mc.player.getUniqueID()).setGameType(GameType.SPECTATOR);

        x = (float) mc.player.getPosX();
        y = (float) mc.player.getPosY();
        z = (float) mc.player.getPosZ();

        final RemoteClientPlayerEntity fakePlayer = new RemoteClientPlayerEntity(mc.world, new GameProfile(UUID.randomUUID(), mc.getSession().getUsername()));

        fakePlayer.inventory = mc.player.inventory;
        fakePlayer.setHealth(mc.player.getHealth());
        fakePlayer.setPositionAndRotation(x, mc.player.getBoundingBox().minY, z, mc.player.rotationYaw, mc.player.rotationPitch);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        mc.world.addEntity(TEMP_ENTITY_ID, fakePlayer);
        mc.player.setGameType(GameType.ADVENTURE);
        mc.player.setSneaking(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.connection.getPlayerInfo(mc.player.getUniqueID()).setGameType(prev);

        mc.player.setMotion(0, 0, 0);
        mc.player.setVelocity(0, 0, 0);
        mc.player.setPosition(x, y, z);
        mc.player.setSneaking(false);
        mc.world.removeEntityFromWorld(TEMP_ENTITY_ID);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (mc.player == null) {
                toggle();
                return;
            }

            if (!mc.player.isAlive()) {
                toggle();
            }

            if (mc.world == null) return;
            final IPacket<?> packet = e.getPacket();

            if (e.isSendPacket()) {
                if (packet instanceof CPlayerPacket || packet instanceof CPlayerAbilitiesPacket) {
                    e.setCancel(true);
                }
            }

            if (e.isReceivePacket()) {
                if (packet instanceof SPlayerPositionLookPacket) {
                    e.setCancel(true);
                }
            }
        }

        if (event instanceof EventLivingUpdate) {
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motion.y = -speed.get() * 0.75F;
            } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motion.y = speed.get() * 0.75F;
            } else {
                mc.player.setMotion(0, 0, 0);
            }

            MoveUtil.setSpeed(speed.get());
        }

        if (event instanceof Render2DEvent) {
            if (!coordinates.get()) return;
            
            float xPosition = (float) (mc.player.getPosX() - x);
            float yPosition = (float) (mc.player.getPosY() - y);
            float zPosition = (float) (mc.player.getPosZ() - z);

            float centerX = mc.getMainWindow().getScaledWidth() / 2F;
            float centerY = mc.getMainWindow().getScaledHeight() / 2F;

            String coords = String.format("x: %s y: %s z: %s", 
                Mathf.round(xPosition, 1), 
                Mathf.round(yPosition, 1), 
                Mathf.round(zPosition, 1));
            
            float textWidth = Fonts.SUISSEINTL.get(14).getStringWidth(coords);
            Fonts.SUISSEINTL.get(14).drawString(coords, centerX - textWidth / 2F, centerY + 18, -1);
        }
    }
}