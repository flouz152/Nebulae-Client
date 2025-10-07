package beame.components.modules.misc;

import beame.Essence;
import beame.components.command.AbstractCommand;
import beame.feature.notify.NotificationManager;
import beame.util.ClientHelper;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventMotion;
import events.impl.player.EventOverlaysRender;
import events.impl.player.EventUpdate;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;


import java.util.*;
import java.util.stream.Collectors;

public class MinecraftUtils extends Module {
// leaked by itskekoff; discord.gg/sk3d v8ucfLEd
    private final Map<UUID, Set<EffectInstance>> playerEffects = new HashMap<>();
    private boolean confirmedHubExit = false;
    private int balance = -1;
    private final Set<String> notifiedEffects = new HashSet<>();
    private boolean lowHealthNotified = false;

    public final EnumSetting removals = new EnumSetting("Убирать",
            new BooleanSetting("Огонь", true, 0),
            new BooleanSetting("Боссбар", false, 0),
            new BooleanSetting("Скорборд", false, 0),
            new BooleanSetting("Тайтл", false, 0),
            new BooleanSetting("Снесение тотема", true, 0),
            new BooleanSetting("Туман", false, 0),
            new BooleanSetting("Плохие эффекты", true, 0),
            new BooleanSetting("Погода", true, 0),
            new BooleanSetting("Оверлей блока", true, 0),
            new BooleanSetting("Оверлей воды", true, 0)
            //    new BooleanSetting("Эффект прыгучести", true, 0),
        //new BooleanSetting("Эффект левитации", true, 0),
        //new BooleanSetting("Эффект плавного падения", true, 0)
    );
    public final EnumSetting helper = new EnumSetting("Уведомлять о",
            new BooleanSetting("Выходе в кт (/hub)", true, 0),
            new BooleanSetting("Зельях полученных игроками", false, 0),
            new BooleanSetting("Заканчивающихся зельях", true, 0),
            new BooleanSetting("Просьбе спека", true, 0),
            new BooleanSetting("Низком количестве хп", true, 0)
    );
    public final EnumSetting optimize = new EnumSetting("Оптимизиация",
            new BooleanSetting("Небо и облака", true, 0),
            new BooleanSetting("Тени", true, 0),
            new BooleanSetting("Анимации", true, 0),
            new BooleanSetting("Ентити", true, 0),
            new BooleanSetting("Свечение игроков", true, 0)
    );
    public final BooleanSetting noHurtCum = new BooleanSetting("Тряска камеры", true, 0);
    private final BooleanSetting noJumpDelay = new BooleanSetting("Заддержка прыжка", true, 0);

    public MinecraftUtils() {
        super("MinecraftUtils", Category.Misc, true, "Вспомогательные утилиты для оптимизации игры");
        addSettings(removals, helper, optimize, noJumpDelay, noHurtCum);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventOverlaysRender) {
            EventOverlaysRender.OverlayType overlayType = ((EventOverlaysRender) event).getOverlayType();
            if (this.removals.get(0).get() && overlayType.equals((Object) EventOverlaysRender.OverlayType.FIRE_OVERLAY)) {
                event.setCancel(true);
            } else if (this.removals.get(1).get() && overlayType.equals((Object) EventOverlaysRender.OverlayType.BOSS_LINE)) {
                event.setCancel(true);
            } else if (this.removals.get(2).get() && overlayType.equals((Object) EventOverlaysRender.OverlayType.SCOREBOARD)) {
                event.setCancel(true);
            } else if (this.removals.get(3).get() && overlayType.equals((Object) EventOverlaysRender.OverlayType.TITLES)) {
                event.setCancel(true);
            } else if (this.removals.get(4).get() && overlayType.equals((Object) EventOverlaysRender.OverlayType.TOTEM)) {
                event.setCancel(true);
            } else if (this.removals.get(5).get() && overlayType.equals((Object) EventOverlaysRender.OverlayType.FOG)) {
                event.setCancel(true);
            }
        }
        
        RenderHook.setDisablePlayerGlowing(optimize.get(4).get());
        
        if (event instanceof EventUpdate) {
            if (optimize.get(4).get()) {
                for (net.minecraft.entity.Entity entity : mc.world.getAllEntities()) {
                    if (entity.isGlowing()) {
                        entity.setGlowing(false);
                    }
                }
            }
        }
        
        if (event instanceof Render2DEvent) {
            mc.gameSettings.ofSky = !optimize.get(0).get();
            mc.gameSettings.ofCustomSky = !optimize.get(0).get();

            mc.gameSettings.entityShadows = !optimize.get(1).get();

            mc.gameSettings.ofAnimatedTextures = !optimize.get(2).get();
            mc.gameSettings.ofAnimatedFire = !optimize.get(2).get();
            mc.gameSettings.ofAnimatedExplosion = !optimize.get(2).get();
            mc.gameSettings.ofAnimatedFlame = !optimize.get(2).get();
            mc.gameSettings.ofAnimatedPortal = !optimize.get(2).get();
            mc.gameSettings.ofAnimatedRedstone = !optimize.get(2).get();
            mc.gameSettings.ofAnimatedSmoke = !optimize.get(2).get();
            mc.gameSettings.ofAnimatedTerrain = !optimize.get(2).get();

            mc.gameSettings.ofCustomEntityModels = !optimize.get(3).get();

            if (noJumpDelay.get()) {
                mc.player.jumpTicks = 0;
            }

            boolean isRaining = this.removals.get(7).get() && mc.world.isRaining();
            boolean hasEffects = this.removals.get(6).get() && (mc.player.isPotionActive(Effects.BLINDNESS) || mc.player.isPotionActive(Effects.NAUSEA));
            if (isRaining) {
                mc.world.setRainStrength(0.0f);
                mc.world.setThunderStrength(0.0f);
            }
            if (hasEffects) {
                mc.player.removePotionEffect(Effects.NAUSEA);
                mc.player.removePotionEffect(Effects.BLINDNESS);
            }

            if (helper.get(1).get()) {
                checkEffectChanges();
            }

            checkPotionDurations();

            checkLowHealth();
        }
        
        if (event instanceof EventPacket eventPacket) {
            if (mc.player == null) return;

            if (eventPacket.getPacket() instanceof net.minecraft.network.play.server.SChatPacket && helper.get(3).get()) {
                net.minecraft.network.play.server.SChatPacket chatPacket = (net.minecraft.network.play.server.SChatPacket) eventPacket.getPacket();
                ITextComponent component = chatPacket.getChatComponent();
                checkForSpecRequest(component.getString(), component);
            }

            if (!eventPacket.isSendPacket()) return;

            if (eventPacket.getPacket() instanceof CChatMessagePacket p) {
                String message = p.getMessage().toLowerCase();
                if (mc.player == null) return;

                message = message.toLowerCase();

                if (message.equals("/hub") && helper.get(0).get() && ClientHelper.isPvP()) {
                    if (!confirmedHubExit) {
                        AbstractCommand.addMessage("Вы находитесь в кт, Вы точно хотите выйти? Напишите /hub еще 1 раз");
                        confirmedHubExit = true;
                        eventPacket.cancel();
                    } else {
                        confirmedHubExit = false;
                    }
                } else if (message.startsWith("/an") && helper.get(0).get() && ClientHelper.isPvP()) {
                    String number = message.substring(3);
                    if (number.matches("\\d+")) {
                        if (!confirmedHubExit) {
                            AbstractCommand.addMessage("Вы находитесь в кт, Вы точно хотите перейти на другую анархию? Напишите " + message + " еще 1 раз");
                            confirmedHubExit = true;
                            eventPacket.cancel();
                        } else {
                            confirmedHubExit = false;
                        }
                    }
                } else {
                    confirmedHubExit = false;
                }
                if (event instanceof EventMotion eventMotion) {
                    if (removals.get("Эффект прыгучести").get()) {
                        mc.player.removePotionEffect(Effects.JUMP_BOOST);
                    }

                    if (removals.get("Эффект левитации").get()) {
                        mc.player.removePotionEffect(Effects.LEVITATION);
                    }

                    if (removals.get("Эффект плавного падения").get()) {
                        mc.player.removePotionEffect(Effects.SLOW_FALLING);
                    }
                }
            }
        }
    }

    private void checkEffectChanges() {
        if (mc.world == null || mc.player == null) return;

        PlayerEntity localPlayer = mc.player;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!isPlayerInRadius(localPlayer, player, 100)) {
                continue;
            }

            UUID playerId = player.getUniqueID();
            Collection<EffectInstance> currentEffects = player.getActivePotionEffects();

            Set<String> currentEffectKeys = currentEffects.stream()
                    .map(e -> e.getPotion().getName() + ":" + e.getAmplifier())
                    .collect(Collectors.toSet());

            Set<String> previousEffectKeys = playerEffects.getOrDefault(playerId, Collections.emptySet())
                    .stream()
                    .map(e -> e.getPotion().getName() + ":" + e.getAmplifier())
                    .collect(Collectors.toSet());

            Set<String> newEffectKeys = new HashSet<>(currentEffectKeys);
            newEffectKeys.removeAll(previousEffectKeys);


            Set<String> effectLines = new LinkedHashSet<>();
            for (EffectInstance effect : currentEffects) {
                String key = effect.getPotion().getName() + ":" + effect.getAmplifier();
                if (newEffectKeys.contains(key)) {
                    String localizedName = I18n.format(effect.getPotion().getName());
                    String duration = getPotionDurationString(effect, 1);
                    int level = effect.getAmplifier() + 1;

                    String line = TextFormatting.RED + localizedName + " " + level + TextFormatting.WHITE + " на " + TextFormatting.RED + duration;

                    effectLines.add(line);
                }
            }

            if (!effectLines.isEmpty()) {
                String playerName = player.getName().getString();
                String header = "Игрок " + playerName + " получил "
                        + (effectLines.size() == 1 ? "эффект:" : "эффекты" + ":");
                AbstractCommand.addMessage(header);
                for (String line : effectLines) {
                    AbstractCommand.addMessage(line);
                }
            }

            playerEffects.put(playerId, new HashSet<>(currentEffects));
        }
    }

    private boolean isBadEffect(EffectInstance effect) {
        return effect.getPotion() == Effects.SLOWNESS
                || effect.getPotion() == Effects.BLINDNESS
                || effect.getPotion() == Effects.WEAKNESS
                || effect.getPotion() == Effects.WITHER
                || effect.getPotion() == Effects.POISON
                || effect.getPotion() == Effects.MINING_FATIGUE
                || effect.getPotion() == Effects.NAUSEA
                || effect.getPotion() == Effects.UNLUCK
                || effect.getPotion() == Effects.HUNGER;
    }

    private boolean isPlayerInRadius(PlayerEntity player1, PlayerEntity player2, double radius) {
        return player1.getDistanceSq(player2) <= radius * radius;
    }

    private String getPotionDurationString(EffectInstance effect, int multiplier) {
        if (effect.getIsPotionDurationMax()) return "∞";
        int duration = effect.getDuration() / multiplier;
        int seconds = duration / 20;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes > 0 ? minutes + " мин " + seconds + " сек" : seconds + " сек";
    }

    private void checkPotionDurations() {
        if (!helper.get(2).get() || mc.player == null) return;

        for (EffectInstance effect : mc.player.getActivePotionEffects()) {
            String effectKey = effect.getPotion().getName() + ":" + effect.getAmplifier();
            if (effect.getDuration() < 200 && isTrackedPotion(effect) && !notifiedEffects.contains(effectKey)) {
                String potionName = I18n.format(effect.getPotion().getName());
                AbstractCommand.addMessage("Скоро закончится зелье " + potionName);
                Essence.getHandler().notificationManager.pushNotify("Скоро закончится зелье " + potionName, NotificationManager.Type.Info);
                notifiedEffects.add(effectKey);
            }
        }
        notifiedEffects.removeIf(effectKey -> mc.player.getActivePotionEffects().stream().noneMatch(effect ->
                (effect.getPotion().getName() + ":" + effect.getAmplifier()).equals(effectKey)));
    }

    private boolean isTrackedPotion(EffectInstance effect) {
        return effect.getPotion() == Effects.SPEED ||
                effect.getPotion() == Effects.STRENGTH ||
                effect.getPotion() == Effects.FIRE_RESISTANCE;
    }

    private void checkForSpecRequest(String message, ITextComponent component) {
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("спек") || lowerMessage.contains("spec")) {
            String playerName = "";
            if (message.contains("⇨")) {
                int arrowIndex = message.indexOf("⇨");
                int prefixEnd = message.lastIndexOf("]", arrowIndex);
                if (prefixEnd > 0) {
                    playerName = message.substring(prefixEnd + 1, arrowIndex).trim();
                }
            }

            if (playerName.isEmpty()) {
                playerName = "Кто-то";
            }
            playerName = playerName.replaceAll("§[0-9a-fk-or]", "");
            boolean isOwnMessage = false;
            if (mc.getSession() != null) {
                isOwnMessage = playerName.equals(mc.getSession().getUsername());
            }
            if (mc.player != null && !isOwnMessage) {
                isOwnMessage = playerName.equals(mc.player.getName().getString());
            }

            if (!isOwnMessage) {
                String notifyMessage = playerName + " попросил о спеке";
                AbstractCommand.addMessage(notifyMessage);
                Essence.getHandler().notificationManager.pushNotify(notifyMessage, NotificationManager.Type.Staff);
            }
        }
    }

    private void checkLowHealth() {
        if (mc.player == null) return;

        if (helper.get(4).get()) {
            if (mc.player.getHealth() <= 8.0f) {
                if (!lowHealthNotified) {
                    AbstractCommand.addMessage("§c§lВнимание! §rНизкий уровень здоровья!");
                    Essence.getHandler().notificationManager.pushNotify("Низкий уровень здоровья!", NotificationManager.Type.Staff);
                    lowHealthNotified = true;
                }
            } else {
                lowHealthNotified = false;
            }
        }
    }

    public static boolean isValidPosition(net.minecraft.util.math.vector.Vector3d pos) {
        if (mc.world == null) return false;
        if (pos.y < 0 || pos.y > 256) return false;
        net.minecraft.util.math.BlockPos blockPos = new net.minecraft.util.math.BlockPos(pos.x, pos.y, pos.z);
        return mc.world.getBlockState(blockPos).getMaterial().isReplaceable() &&
                mc.world.getBlockState(blockPos.up()).getMaterial().isReplaceable();
    }
}
