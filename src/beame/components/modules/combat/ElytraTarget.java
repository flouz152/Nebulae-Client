/*
package beame.components.modules.combat;

import static beame.util.IMinecraft.mc;
import static org.lwjgl.opengl.GL11.glLineWidth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import beame.Essence;
import beame.components.command.AbstractCommand;
import beame.components.modules.combat.Aura;
import beame.feature.notify.NotificationManager;
import beame.util.ClientHelper;
import beame.util.math.IdealHitUtility;
import beame.util.math.MathUtil;
import beame.util.math.TimerUtil;
import beame.util.other.BoostUtility;
import beame.util.player.InventoryUtility;
import beame.util.player.PlayerUtil;
import events.Event;
import events.EventKey;
import events.impl.game.EventSpawn;
import events.impl.player.*;
import events.impl.render.Render3DPosedEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket.Action;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.Config;
import net.optifine.shaders.Shaders;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.SliderSetting;


@Getter
public class ElytraTarget extends Module {

    public final EnumSetting utilities = new EnumSetting( "Утилиты",
      new BooleanSetting("Замораживать игрока", true),
          new BooleanSetting("Следовать за перлами", true),
          new BooleanSetting("Менять цель на ударяющего", true),
          new BooleanSetting("Фейерверк при замедлении", true),
          new BooleanSetting("Надевать элитру", true),
          new BooleanSetting("Resolver", true),
          new BooleanSetting("Предикт", true),
          new BooleanSetting("Сменять нагрудник если падаешь", true));


    final BooleanSetting predictDefensive = new BooleanSetting( "Анти Defensive").setVisible(() -> utilities.get("Предикт").get());
    final BooleanSetting span = new BooleanSetting( "Перегон").setVisible(() -> utilities.get("Предикт").get());
    final BooleanSetting doubleRot = new BooleanSetting( "Двойная ротация").setVisible(() -> span.get());

     public final EnumSetting autoleave = new EnumSetting( "Улетать",
             new BooleanSetting("При маленьком здоровье", true),
             new BooleanSetting("Когда ещё не можешь ударить", true),
             new BooleanSetting("При использовании предметов", true),
             new BooleanSetting("При нажатии клавиши", true));




    final BooleanSetting notUse = new BooleanSetting("Минимизировать траты").setVisible(() -> utilities.get("Замораживать игрока").get());


    final SliderSetting useCooldown = new SliderSetting("Задержка фейерверка",500,50,6000,100);

    final BooleanSetting swapChest = new BooleanSetting( "Свапать нагрудник").setVisible(() -> utilities.get("Замораживать игрока").get());

    final SliderSetting useCooldownLeave = new SliderSetting( "Задержка при ливе",500,50,3000,100).setVisible(() -> !autoleave.get().isEmpty());
    final BooleanSetting onlyGround = new BooleanSetting( "Только на земле").setVisible(() -> !autoleave.get("При маленьком здоровье").get());
    final SliderSetting leaveHealth = new SliderSetting( "Здоровье для лива",10,1,20,0.5f).setVisible(() -> autoleave.get("При маленьком здоровье").get());

    public final EnumSetting visuals = new EnumSetting( "Визуалы",
            new BooleanSetting("Линии отлива", false).setVisible(() -> !autoleave.get().isEmpty()
            ), new BooleanSetting("Линия полёта", false),
            new BooleanSetting("Миссы противника", false),
            new BooleanSetting("Настоящая позиция противника", true));


    public final SliderSetting leaveVecX = new SliderSetting("Вектор направления X", 0, -50, 50, 1).setVisible(() -> autoleave.get("При маленьком здоровье").get() || autoleave.get("Когда ещё не можешь ударить").get()
            ||autoleave.get("При использовании предметов").get() || autoleave.get("При нажатии клавиши").get());
    public final SliderSetting leaveVecY = new SliderSetting("Вектор направления Y", 20, -50, 50, 1).setVisible(() -> autoleave.get("При маленьком здоровье").get() || autoleave.get("Когда ещё не можешь ударить").get()
            ||autoleave.get("При использовании предметов").get() || autoleave.get("При нажатии клавиши").get());;
    public final SliderSetting leaveVecZ = new SliderSetting("Вектор направления Z", 0, -50, 50, 1).setVisible(() -> autoleave.get("При маленьком здоровье").get() || autoleave.get("Когда ещё не можешь ударить").get()
            ||autoleave.get("При использовании предметов").get() || autoleave.get("При нажатии клавиши").get());;


    public final EnumSetting desync = new EnumSetting( "Десинхронизация при",
    new BooleanSetting ("Ударе (Defensive)",true));


    final BooleanSetting swapVector = new BooleanSetting( "Менять вектор", true).setVisible(() -> autoleave.get("При маленьком здоровье").get() || autoleave.get("Когда ещё не можешь ударить").get()
            ||autoleave.get("При использовании предметов").get() || autoleave.get("При нажатии клавиши").get());


    final BindSetting changeTarget = new BindSetting("Смена цели", 0);


    final BindSetting leaveBind = new BindSetting( "Бинд отлёта",0).setVisible(() -> autoleave.get("При нажатии клавиши").get());


    final SliderSetting slot = new SliderSetting("Выбор слота",7,1,9,1);


    final TimerUtil useTimer = new TimerUtil();

    Entity pearlEntity;
    Vector3d lastPos;
    boolean prevFreezed;

    Vector3d leaveVec = Vector3d.ZERO;
    Vector3d lastVec = Vector3d.ZERO;

    Vector3d defensivePos;
    @Getter
    boolean defensiveActive, lastDefensive;
    final ArrayList<IPacket<?>> packets = new ArrayList<>();
    final TimerUtil defensiveTimer = new TimerUtil();

    ItemStack currentStack = ItemStack.EMPTY;
    boolean bindLeaving;


    public ElytraTarget() {
        super("ElytraTarget", Category.Combat, true, "Автоматически догоняет противника на элитре.");
        addSettings(utilities, predictDefensive,span,doubleRot,autoleave,notUse,useCooldown,swapChest,useCooldownLeave,onlyGround,leaveHealth, leaveVecX, leaveVecY, leaveVecZ);
    }
    @Override
    public void event(Event event) {
        try {
            Aura aura = Essence.getHandler().getModuleList().aura;
            LivingEntity target = aura.getTarget();

            if (!PlayerUtil.isInGame()) return;

            if (event instanceof EventUpdate) {
                this.currentStack = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);

                if (utilities.get("Сменять нагрудник если падаешь").get() && mc.player.getMotion().y > 4 && target != null && target.getMotion().y > 4 &&
                        (Math.abs(mc.player.getPosY() - target.getPosY()) > 5)) {
                    changeChestPlate(currentStack);
                } else if (utilities.get("Сменять нагрудник если падаешь").get() && target != null && target.getMotion().y < 5) {
                    changeChestPlate(currentStack);
                }

                if (event instanceof EventMotion) {
                    //	mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.START_SPRINTING));
                    //	mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.RELEASE_SHIFT_KEY));
                }

                if (PlayerUtil.findItem(45, Items.ELYTRA) == -1) {
                    //	Chat.msg("Элитры нет.");
                    //	this.set(false);
                    //	return;
                }

                if (InventoryUtility.findItemNoChanges(44, Items.FIREWORK_ROCKET) == -1) {
                    AbstractCommand.addMessage("Фейерверков нет.");
                   // this.set(false);
                    return;
                }
            }

            if (!aura.isState()) {
                aura.setTarget(null);
            }

            if (event instanceof EventKey e && !(mc.currentScreen instanceof ChatScreen) && e.key == changeTarget.get() && !e.isReleased() && aura.isState() && mc.isGameFocused()) {
                if (target != null) {
                    aura.focus((PlayerEntity) target);
                } else if (!mc.world.getPlayers().isEmpty()) {
                    aura.focus(mc.world.getPlayers().get(0));
                }
            }

            if (event instanceof EventKey e && !(mc.currentScreen instanceof ChatScreen) && e.key == leaveBind.get().intValue() && aura.isState() && mc.isGameFocused()) {
                bindLeaving = !e.isReleased();
            }

            if (event instanceof EventUpdate) {

                double motion = Math.hypot(mc.player.getPosY() - mc.player.prevPosY, Math.hypot(mc.player.getPosX() - mc.player.prevPosX, mc.player.getPosZ() - mc.player.prevPosZ)) * 20D;

                if (motion < BoostUtility.lastSpeed && utilities.get("Фейерверк при замедлении").get()) {
                    useFirework();
                }

                BoostUtility.lastSpeed = motion;

                if (target != null && mc.player.getDistanceVec(target.getPosX(), target.getPosY(), target.getPosZ()) < 10) {
                    if (target.lastSwing.passed(100) && !target.lastSwing.passed(400) && target.tryAttack && mc.player.hurtTime == 0 && visuals.get("Миссы противника").get() && !isLeaving(target)) {
                        Essence.getHandler().notificationManager.pushNotify(target.getName().getString() + " missed shot due to resolver", NotificationManager.Type.Info);
                        target.tryAttack = false;
                    }
                }

                for (PlayerEntity ent : mc.world.getPlayers()) {
                    if (mc.player.getDistanceVec(ent.getPosX(), ent.getPosY(), ent.getPosZ()) < 6 && ent.lastSwing.passed(200) && !ent.lastSwing.passed(400) && ent.tryAttack && mc.player.hurtTime > 0 && utilities.get("Менять цель на ударяющего").get()) {
                        aura.focus(ent);

                        ent.tryAttack = false;
                    }
                }
            }

            if (event instanceof EventSpawn e) {
                Entity spawnedEntity = e.getEntity();
                if (spawnedEntity instanceof EnderPearlEntity ent) {
                    mc.world.getPlayers().stream()
                            .min(Comparator.comparingDouble((p) -> p.getDistanceVec(spawnedEntity.getPosX(), spawnedEntity.getPosY(), spawnedEntity.getPosZ()) + (mc.player == p || p.getDistanceVec(spawnedEntity.getPosX(), spawnedEntity.getPosY(), spawnedEntity.getPosZ()) > 5 ? 10000 : 0)))
                            .ifPresent((player) -> {
                                if (aura.getPrevTarget() != null && player == aura.getPrevTarget() && utilities.get("Следовать за перлами").get() && ent.getDistanceVec(player.getPosX(), player.getPosY(), player.getPosZ()) < 5) {
                                    this.pearlEntity = spawnedEntity;
                                }
                            });
                }
            }

            handleDesync(event);

            if (target != null || this.lastPos != null) {
                boolean leave = canLeave(target);

                for (Entity entity : mc.world.getAllEntities()) {
                    if (entity instanceof EnderCrystalEntity crystalEntity && mc.player.getDistanceVec(crystalEntity.getPosX(), crystalEntity.getPosY(), crystalEntity.getPosZ()) < 10) {
                        leave = true;
                    }
                }

                //defensiveActive = mc.player.getDistance(target) > 4;w

                defensiveActive = !leave;


                Vector3d targetVec = this.lastPos != null ? this.lastPos : getPos(target);
                if (targetVec == null) {
                    targetVec = target.getPositionVec().add(0, target.getEyeHeight(), 0);
                }

                List<Vector3d> leaveVectors = new ArrayList<>();
                leaveVectors.add(target == null ? lastPos.add(new Vector3d(leaveVecX.get(), leaveVecY.get(), leaveVecZ.get())) : target.getEyePosition(0).add(new Vector3d(leaveVecX.get(), leaveVecY.get(), leaveVecZ.get())));

                if (event instanceof Render3DPosedEvent e) {
                    if (leave && visuals.get("Линии отлива").get() && leaveVec != Vector3d.ZERO && target != null) {
                        for (Vector3d vector : leaveVectors) {
                            if (MathUtility.canSeen(vector) && vector.y < 400 && (!swapVector.get() || leaveVec != vector)) {
                                Vector3d start = target.getPositionVec().add(0, mc.player.getHeight() / 2F, 0);
                                Vector3d end = vector;
                                RenderUtil3D.drawLine(
                                    e.getMatrix(),
                                    (float) start.x, (float) start.y, (float) start.z,
                                    (float) end.x, (float) end.y, (float) end.z,
                                    FixColor.GREEN,
                                    2.0f,
                                    true,
                                    true
                                );
                                break;
                            } else {
                                Vector3d start = target.getPositionVec().add(0, mc.player.getHeight() / 2F, 0);
                                Vector3d end = vector;
                                RenderUtil3D.drawLine(
                                    e.getMatrix(),
                                    (float) start.x, (float) start.y, (float) start.z,
                                    (float) end.x, (float) end.y, (float) end.z,
                                    FixColor.RED,
                                    2.0f,
                                    true,
                                    true
                                );
                            }
                        }
                    }

                    if (visuals.get("Линия полёта").get() && !leave) {
                        Vector3d start = mc.player.getPositionVec().add(0, mc.player.getHeight() / 2F, 0);
                        Vector3d end = targetVec;
                        RenderUtil3D.drawLine(
                            e.getMatrix(),
                            (float) start.x, (float) start.y, (float) start.z,
                            (float) end.x, (float) end.y, (float) end.z,
                            FixColor.GREEN,
                            2.0f,
                            true,
                            true
                        );
                    }
                }







                // Ротация
                if (event instanceof EventJump || event instanceof EventMove || event instanceof EventTrace || event instanceof EventInput || event instanceof EventMotion) {
                    if (leave && leaveVec == Vector3d.ZERO) {
                        for (Vector3d vector : leaveVectors) {
                            if (MathUtility.canSeen(vector) && vector.y < 400 && (!swapVector.get() || !lastVec.equals(vector))) {
                                leaveVec = vector;
                                break;
                            }
                        }
                    }

                    if (!leave && leaveVec != Vector3d.ZERO) {
                        //	leaveVec = Vector3d.ZERO;
                    }


                    Vector2f rotation = Rotation.get(leave ? leaveVec : targetVec);


                    //if (this.lastPos != null || leave) {
                    PlayerUtil.look(event, rotation.x, rotation.y, false);
                    //}
                }






                if (event instanceof EventAttack e) {
                    if (e.getTarget() == target && leave) {
                        //	useFirework();
                    }

                    for (Vector3d vector : leaveVectors) {
                        if (MathUtil.canSeen(vector) && vector.y < 255 && (!swapVector.get() || !lastVec.equals(vector.sub(target.getEyePosition(0)))) && vector.distanceTo(mc.player.getPositionVec()) < 2.5f) {
                            leaveVec = vector;
                            //Chat.debug("Вектор изменен на " + leaveVec.sub(target.getEyePosition(0)));
                            break;
                        }
                    }

                    if (doubleRot.get() && target != null &&
                        mc.player.getDistanceVec(target.getPosX(), target.getPosY(), target.getPosZ()) >
                        mc.player.getDistanceVec(
                            target.getPositionVec().add(target.getMotion()).x,
                            target.getPositionVec().add(target.getMotion()).y,
                            target.getPositionVec().add(target.getMotion()).z
                        ) &&
                        isLeaving(target)) {
                        Vector3d pos = mc.player.getPositionVec();
                        Vector2f rotation = Rotation.get(target.getPositionVec().add(0, target.getHeight()/2F, 0));
                        Bypass.send(pos.x, pos.y - 1e-6, pos.z, rotation.x, rotation.y, false);
                    }

                    lastVec = leaveVec.sub(target.getEyePosition(0));
                }

                if (event instanceof EventRenderWorldEntities e && resolverPos.get() && target != null && target.getPositionVec() != null) {
                    this.drawEntity3D(e.getMatrix(), target, getPos(target), 0.2f);
                }

                if (event instanceof EventMove e) {
                    for (Vector3d vector : leaveVectors) {
                        if (vector.equals(leaveVec)) {
                            leaveVec = vector;
                            break;
                        }
                    }
                    boolean canFreeze = (this.utilities.get("Замораживать игрока").get() && target != null && mc.player.getDistanceVec(target.getPosX(), target.getPosY(), target.getPosZ()) < 3 && !(leave)) || leave && mc.player.getDistanceVec(leaveVec.x, leaveVec.y, leaveVec.z) < 1;

                    if (canFreeze) {
                        e.setMotion(Vector3d.ZERO);
                    }
                    if (utilities.get("Надевать элитру").get()) {
                        if (canFreeze && PlayerUtil.getChest() == Items.ELYTRA) {
                            int item = InventoryUtility.getChestplate();
                            PlayerUtil.moveItem(item < 46 ? item : 6, 6, true);
                        }

                        if (!canFreeze && PlayerUtil.getChest() != Items.ELYTRA) {
                            int item = PlayerUtil.findItem(45, Items.ELYTRA);
                            PlayerUtil.moveItem(item < 46 ? item : 6, 6, true);
                        }
                    }

                    prevFreezed = canFreeze;
                }

                if (event instanceof EventUpdate) {
                    if (mc.world.getAllEntities().contains(pearlEntity)) {
                        lastPos = this.pearlEntity.getPositionVec();
                    } else {
                        if (this.lastPos != null && mc.player.getDistanceVec(this.lastPos.x, this.lastPos.y, this.lastPos.z) < 3) {
                            this.lastPos = null;
                            this.pearlEntity = null;
                        }
                    }

                    boolean elytra = PlayerUtil.find(38).getItem() == Items.ELYTRA;
                    if (elytra && !mc.player.isHandActive()) {
                        if (mc.player.isElytraFlying()) {
                            boolean canFreeze = (this.utilities.get("Замораживать игрока").get() && target != null && mc.player.getDistanceVec(target.getPosX(), target.getPosY(), target.getPosZ()) < 3 && mc.player.isElytraFlying() && !(leave)) || leave && mc.player.getDistanceVec(leaveVec.x, leaveVec.y, leaveVec.z) < 1;

                            boolean canUse = !(canFreeze && notUse.get());
                            if (
              !(EventMotion2.LAST_PITCH > 0 && isLeaving(target)) &&
             this.useTimer.passed(leave && mc.player.getDistanceVec(target.getPosX(), target.getPosY(), target.getPosZ()) < 6 ? useCooldownLeave.get() : (long) useCooldown.get()) && canUse) {
                                useFirework();
                            }
                        } else {
                            if (mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isPressed()) {
                                mc.player.jump();
                            } else {
                                if (mc.player.fallDistance > 0.08f) {
                                    mc.player.startFallFlying();
                                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                                    useFirework();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            AbstractCommand.addMessage("чек консоль");
            System.out.print(e);
        }
    }
    private void handleDesync(Event event) {
        Aura aura = Essence.getHandler().getModuleList().aura;
        LivingEntity target = aura.getTarget();
        Vector3d closestPoint = null;
        if (target != null) {
            try {
                closestPoint = AuraUtil.getClosestTargetPoint(target);
            } catch (Exception ex) {
                System.out.println("[ElytraTarget] Exception в getClosestTargetPoint: " + ex);
                ex.printStackTrace();
            }
            if (closestPoint == null) {
                System.out.println("[ElytraTarget] getClosestTargetPoint вернул null для target: " + target);
            }
        } else {
            System.out.println("[ElytraTarget] target is null перед getClosestTargetPoint");
        }

        boolean canUse = closestPoint != null && mc.player.getDistance(closestPoint) < 20 && !isLeaving(target);

        if (event instanceof EventUpdate && target != null) {
            //	Chat.debug(isLeaving(target));
        }

        if (event instanceof EventPacket e && e.isReceivePacket() && desync.get("Ударе (Defensive)").get() && defensiveActive && !mc.isSingleplayer() && canUse) {
            //if (!(e.getPacket() instanceof CPlayerPacket.PositionPacket && e.getPacket() instanceof CPlayerPacket.PositionRotationPacket && e.getPacket() instanceof CPlayerPacket.RotationPacket
            //		&& e.getPacket() instanceof CUseEntityPacket && e.getPacket() instanceof CAnimateHandPacket))

            packets.add(e.getPacket());
            event.cancel();
        }

        if (event instanceof EventReceivePacket e) {
            if (e.getPacket() instanceof SPlayerPositionLookPacket) {
                //Chat.debug(defensiveActive + " - " + defensiveTimer.getElapsed());
            }
        }

        if (event instanceof EventUpdate) {
            if ((!desync.get("Ударе (Defensive)").get()) || !defensiveActive || defensiveTimer.passed(1000) || !canUse) && !mc.isSingleplayer()) {
                for (IPacket<?> p : packets) {
                    mc.player.connection.sendPacketSilent(p);
                }
                packets.clear();
                defensivePos = mc.player.getPositionVec();
                defensiveTimer.reset();
            }

            if (!lastDefensive && defensiveActive) {
                defensivePos = mc.player.getPositionVec();
                defensiveTimer.reset();
            }

            lastDefensive = defensiveActive;
        }

        if (event instanceof EventRenderWorldEntities e && desync.get("Ударе (Defensive)").get() && defensiveActive && defensivePos != null && canUse) {
            Render.drawEntity3D(e.getMatrix(), mc.player, defensivePos, 0.2f);
        }
    }

    public boolean isLeaving(LivingEntity target) {
        //mc.player.getDistance(target.getPositionVec()) < mc.player.getDistance(target.getPositionVec().add(target.getMotion()))
        if (target.isElytraFlying() && target.lastSwing.passed(2000))
            target.leaving.reset();

        return target.lastSwing.passed(2000) && target.isElytraFlying();
    }
    private boolean canLeave(LivingEntity target) {
        if (target == null || isLeaving(target))
            return false;

        if (autoleave.get("При маленьком здоровье").get() && (target.isElytraFlying() || !onlyGround.get()) && (mc.player.getHealth() + mc.player.getAbsorptionAmount() < leaveHealth.get()))
            return true;

        if (autoleave.get("Когда ещё не можешь ударить").get() && mc.player.getCooledAttackStrength() < IdealHitUtility.getAICooldown())
            return true;

        if (autoleave.get("При использовании предмето").get() && mc.player.isHandActive())
            return true;

        if (autoleave.get("При нажатии клавиши").get() && bindLeaving)
            return true;

        return false;
    }
    private void useFirework() {
        int firework = InventoryUtility.findItemNoChanges(44, Items.FIREWORK_ROCKET);
        if (firework >= 0) {
            if (firework != 45) {
                if (firework < 9) {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(firework));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                } else {
                    InventoryUtility.moveItem(firework, 35 + (int) slot.get(), true);
                    mc.player.connection.sendPacket(new CHeldItemChangePacket((int) slot.get() - 1));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                }
            } else {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            }

            this.useTimer.reset();
        } else {
            AbstractCommand.addMessage("Фейерверков нет.");
            onDisable();
            this.set(false);
        }
    }

    private void drawLine3D(MatrixStack ms, Vector3d from, Vector3d to, FixColor color) {
        if (from == null || to == null) return;

        ms.push();

        // Устанавливаем режимы отрисовки (чтобы все заебись было тип)
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        GL11.glDepthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0);

        Vector3d renderOffset = mc.getRenderManager().info.getProjectedView();

        // Меняем позицию на позицию отрисовки(чтобы они не рендерились в пизде)
        ms.translate(-renderOffset.x, -renderOffset.y, -renderOffset.z);

        glLineWidth(3);

        BUILDER.begin(1, DefaultVertexFormats.POSITION_COLOR);

        Matrix4f matrix = ms.getLast().getMatrix();

        BUILDER.pos(matrix, (float) from.x, (float) from.y, (float) from.z).color(color).endVertex();
        BUILDER.pos(matrix, (float) to.x, (float) to.y, (float) to.z).color(color).endVertex();


        TESSELLATOR.draw();

        // Выключаем режимы рендеринга
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        Render.resetColor();

        ms.pop();
    }

    @Override
    public void onEnable() {
        if (utilities.get("Надевать элитру").get() && PlayerUtil.find(38).getItem() != Items.ELYTRA) {
            int item = PlayerUtil.findItem(45, Items.ELYTRA);
            PlayerUtil.moveItemOld(item < 46 ? item : 6, 6, true);
        }
    }


    @Override
    public void onDisable() {
        if (utilities.get("Надевать элитру").get() &&  PlayerUtil.find(38).getItem() == Items.ELYTRA) {
            int item = InventoryUtility.getChestplate();
            PlayerUtil.moveItemOld(item < 46 ? item : 6, 6, true);
        }
        this.lastPos = null;
        this.pearlEntity = null;
    }

    public float getRange() {
        return get() ? 125 : 0;
    }

    public Vector3d getPos(LivingEntity entity) {
        Map<String, Integer> pings = PlayerTabOverlayGui.getPlayerPings();
        String targetName = entity.getName().getString();
        int targetPing = pings.containsKey(targetName) ? pings.get(targetName) : 0;
        Vector3d defaultPos = utilities.get("Resolver").get() && entity.getPositionVec() != null ? entity.getPositionVec() : entity.getPositionVec();

        if (!utilities.get("Предикт").get()) {
            return defaultPos;
        }

        defaultPos = predictDefensive.get() && entity.getPositionVec() != null && entity.isElytraFlying()
                ? PredictUtility.predictElytraPos(entity, entity.getPositionVec(), (int) (entity.getLastResolve().getElapsed()/50))
                : entity.getPositionVec();

        Vector3d leavePos = span.get() && isLeaving(entity)
                ? entity.getPositionVec().add(entity.getMotion().scale(2 + Server.ping() / 50F)) // PredictUtility.predictElytraPos(entity, entity.getPositionVec(), targetPing/50+5)
                : defaultPos;

        return utilities.get("Resolver").get() && entity.getPositionVec() != null
                ? leavePos
                : entity.getPositionVec();
    }

    private BlockPos calcTrajectory(Entity e) {
        return traceTrajectory(e.getPosX(), e.getPosY(), e.getPosZ(), e.getMotion().x, e.getMotion().y, e.getMotion().z);
    }

    private BlockPos traceTrajectory(double x, double y, double z, double mx, double my, double mz) {
        Vector3d lastPos;
        for (int i = 0; i < 300; i++) {
            lastPos = new Vector3d(x, y, z);
            x += mx;
            y += my;
            z += mz;
            mx *= 0.99;
            my *= 0.99;
            mz *= 0.99;
            my -= 0.03f;
            Vector3d pos = new Vector3d(x, y, z);
            BlockRayTraceResult bhr = mc.world.rayTraceBlocks(new RayTraceContext(lastPos, pos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, mc.player));
            if (bhr != null && bhr.getType() == RayTraceResult.Type.BLOCK) return bhr.getPos();

            for (Entity ent : mc.world.getAllEntities()) {
                if (ent instanceof ArrowEntity || ent == mc.player || ent instanceof EnderPearlEntity) continue;
                if (ent.getBoundingBox().intersects(new AxisAlignedBB(x - 0.3, y - 0.3, z - 0.3, x + 0.3, y + 0.3, z + 0.2)))
                    return null;
            }

            if (y <= -65) break;
        }
        return null;
    }

    public float overrideRange(LivingEntity target) {
        return ClientHelper.isConnectedToServer("Bravo") && doubleRot.get() && target != null && mc.player.getDistanceVec(target.getPosX(), target.getPosY(), target.getPosZ()) > mc.player.getDistanceVec(target.getPosX(), target.getPosY(), target.getPosZ()) && isLeaving(target) ? 6 : 3;
    }

    public boolean canAttack(LivingEntity target) {
        return true;//AuraUtility.distanceTo(AuraUtility.getPoint(target)) < 2.5f && target.getLastResolve().getElapsed() <= 50L || !isLeaving(target) || !get();
    }

    private void changeChestPlate(ItemStack stack) {
        if (mc.currentScreen != null) {
            return;
        }
        if (stack.getItem() != Items.ELYTRA) {
            int elytraSlot = getItemSlot(Items.ELYTRA);
            if (elytraSlot >= 0) {
                InventoryUtility.moveItem(elytraSlot, 6);
                return;
            }
        }
        int armorSlot = getChestPlateSlot();
        if (armorSlot >= 0) {
            InventoryUtility.moveItem(armorSlot, 6);
        }
    }

    private int getItemSlot(Item input) {
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == input) {
                slot = i;
                break;
            }
        }
        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }
        return slot;
    }

    private int getChestPlateSlot() {
        Item[] items = {Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE};

        for (Item item : items) {
            for (int i = 0; i < 36; ++i) {
                Item stack = mc.player.inventory.getStackInSlot(i).getItem();
                if (stack == item) {
                    if (i < 9) {
                        i += 36;
                    }
                    return i;
                }
            }
        }
        return -1;
    }

    public void drawEntity3D(MatrixStack ms, LivingEntity player, Vector3d pos, float alpha) {
        ActiveRenderInfo activeRenderInfo = mc.gameRenderer.getActiveRenderInfo();
        ms.push();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture();
        GL11.glShadeModel(7425);
        GlStateManager.disableCull();
        GlStateManager.enableDepthTest();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
        GlStateManager.glBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE.param, GlStateManager.SourceFactor.ZERO.param, GlStateManager.DestFactor.ONE.param);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();
        try {
            double x = pos.x - cameraPos.getX(),
                    y = pos.y - cameraPos.getY(),
                    z = pos.z - cameraPos.getZ();

            EntityRendererManager renderManager = mc.getRenderManager();
            if (renderManager == null)
                return;
            float partialTicks = mc.getRenderPartialTicks();
            Vector3d camera = activeRenderInfo.getProjectedView();
            if (Config.isShaders()) {
                Shaders.nextEntity(player);
            }
            renderManager.renderEntityStatic(
                    player,
                    x,
                    y,
                    z,
                    MathHelper.lerp(partialTicks, player.prevRotationYaw, player.rotationYaw),
                    partialTicks, ms,
                    mc.getRenderTypeBuffers().getBufferSource(),
                    renderManager.getPackedLight(player, partialTicks)
            );
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        GlStateManager.blendFunc(770, 771);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        ms.pop();
    }
}
*/
// leaked by itskekoff; discord.gg/sk3d 45DzPLXk
