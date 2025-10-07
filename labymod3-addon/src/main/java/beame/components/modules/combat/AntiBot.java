package beame.components.modules.combat;

//import beame.util.player.PlayerUtil;
import beame.util.player.PlayerUtil;
import com.mojang.authlib.GameProfile;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.GameUpdateEvent;
import events.impl.player.WorldChangeEvent;
import events.impl.player.WorldLoadEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class AntiBot extends Module {
// leaked by itskekoff; discord.gg/sk3d 4uKS0ShL


    public AntiBot() {
        super("AntiBot", Category.Combat, true, "Отключает анти-чит бота");
    }


    private final Set<UUID> suspectSet = new HashSet<>();
    private static final Set<UUID> botSet = new HashSet<>();


    @Override
    public void event(Event e) {
        if (e instanceof EventPacket event) {
            IPacket<?> packet = event.getPacket();

            if (packet instanceof SPlayerListItemPacket wrapper) {
                if (wrapper.getAction().equals(SPlayerListItemPacket.Action.ADD_PLAYER)) {
                    checkPlayerAfterSpawn(wrapper);
                }
                if (wrapper.getAction().equals(SPlayerListItemPacket.Action.REMOVE_PLAYER)) {
                    removePlayerBecauseLeftServer(wrapper);
                }
            }
        } else if (e instanceof GameUpdateEvent) {
            if (!suspectSet.isEmpty()) {
                mc.world.getPlayers().stream()
                        .filter(player -> suspectSet.contains(player.getUniqueID()))
                        .forEach(this::evaluateSuspectPlayer);
            }
        } else if (e instanceof WorldChangeEvent || e instanceof WorldLoadEvent) {
            reset();
        }
    }

    private void checkPlayerAfterSpawn(SPlayerListItemPacket packet) {
        if (mc.getConnection() == null) return;
        packet.getPlayerAdditionEntries().forEach(data -> {
            GameProfile profile = data.getProfile();
            if (isRealPlayer(data, profile)) {
                return;
            }
            if (isDuplicateProfile(profile)) {
                botSet.add(profile.getId());
            } else {
                suspectSet.add(profile.getId());
            }
        });
    }

    private void removePlayerBecauseLeftServer(SPlayerListItemPacket packet) {
        packet.getPlayers().forEach(data -> {
            suspectSet.remove(data.getProfile().getId());
            botSet.remove(data.getProfile().getId());
        });
    }

    private boolean isRealPlayer(SPlayerListItemPacket.AddPlayerData data, GameProfile profile) {
        return data.getPing() < 5 || (profile.getProperties() != null && !profile.getProperties().isEmpty());
    }

    public boolean isDuplicateProfile(GameProfile profile) {
        return mc.getConnection().getPlayerInfoMap().stream()
                .filter(player -> player.getGameProfile().getName().equals(profile.getName()) && !player.getGameProfile().getId().equals(profile.getId()))
                .count() == 1;
    }

    private void evaluateSuspectPlayer(PlayerEntity player) {
        Iterable<ItemStack> armor = null;

        if (!isFullyEquipped(player)) {
            armor = player.getArmorInventoryList();
        }
        if ((isFullyEquipped(player) || hasArmorChanged(player, armor))) {
            botSet.add(player.getUniqueID());
        }
        suspectSet.remove(player.getUniqueID());
    }

    public boolean isFullyEquipped(PlayerEntity entity) {
        return IntStream.rangeClosed(0, 3)
                .mapToObj(slot -> entity.inventory.armorItemInSlot(slot))
                .allMatch(stack -> stack.getItem() instanceof ArmorItem && !stack.isEnchanted());
    }

    public boolean hasArmorChanged(PlayerEntity entity, Iterable<ItemStack> prevArmor) {
        if (prevArmor == null) {
            return true;
        }

        List<ItemStack> currentArmorList = StreamSupport.stream(entity.getArmorInventoryList().spliterator(), false).toList();
        List<ItemStack> prevArmorList = StreamSupport.stream(prevArmor.spliterator(), false).toList();

        return !IntStream.range(0, Math.min(currentArmorList.size(), prevArmorList.size()))
                .allMatch(i -> currentArmorList.get(i).equals(prevArmorList.get(i))) || currentArmorList.size() != prevArmorList.size();
    }

    public static boolean isBot(PlayerEntity entity) {
        return PlayerUtil.isInvalidName(entity.getGameProfile().getName()) || botSet.contains(entity.getUniqueID());
    }

    public void reset() {
        suspectSet.clear();
        botSet.clear();
    }

    @Override
    public void toggle() {
        super.toggle();
        reset();
    }
}
