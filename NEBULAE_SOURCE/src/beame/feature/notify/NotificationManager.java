package beame.feature.notify;

import beame.Nebulae;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.CustomFont;
import beame.util.fonts.Fonts;
import beame.util.other.SoundUtil;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import beame.module.Module;

import java.util.ArrayList;
import java.util.List;

import static beame.util.IMinecraft.mc;

public class NotificationManager {
// leaked by itskekoff; discord.gg/sk3d enMOQmNU
    List<Notification> notifications = new ArrayList<>();

    private static void addMessage(Object message, Object icon) {
        try {
            //mc.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(TextFormatting.BLUE + "[" + icon + "]" + TextFormatting.GRAY + " " + TextFormatting.RESET + message));
        } catch (Exception exc) { }
    }

    public void pushNotify(String notify, Type type) {
        if(type == Type.Saved) {
            notifications.add(new Notification(notify, type));
            if (Nebulae.getHandler().getModuleList().getClientSounds().isState() && Nebulae.getHandler().getModuleList().getClientSounds().soundActive.get(3).get()) {
                try {
                    SoundUtil.playSound("saved.wav", 65, false);
                } catch (Exception e) {
                    System.out.println("Ошибка воспроизведения звука saved.wav: " + e.getMessage());
                }
            }
        } else if(type == Type.Loaded) {
            notifications.add(new Notification(notify, type));
            if (Nebulae.getHandler().getModuleList().getClientSounds().isState() && Nebulae.getHandler().getModuleList().getClientSounds().soundActive.get(4).get()) {
                try {
                    SoundUtil.playSound("loaded.wav", 65, false);
                } catch (Exception e) {
                    System.out.println("Ошибка воспроизведения звука loaded.wav: " + e.getMessage());
                }
            }
        } else if(type == Type.Staff) {
            notifications.add(new Notification(notify, type));
            if (Nebulae.getHandler().getModuleList().getClientSounds().isState() && Nebulae.getHandler().getModuleList().getClientSounds().soundActive.get(5).get()) {
                try {
                    SoundUtil.playSound("detect.wav", Nebulae.getHandler().getModuleList().getClientSounds().volume.get(), false);
                } catch (Exception e) {
                    System.out.println("Ошибка воспроизведения звука detect.wav: " + e.getMessage());
                }
            }
        } else {
            notifications.add(new Notification(notify));
            if(type == Type.Info) {
                if (Nebulae.getHandler().getModuleList().getClientSounds().isState() && Nebulae.getHandler().getModuleList().getClientSounds().soundActive.get(7).get()) {
                    try {
                        SoundUtil.playSound("notification.wav", Nebulae.getHandler().getModuleList().getClientSounds().volume.get(), false);
                    } catch (Exception e) {
                        System.out.println("Ошибка воспроизведения звука notification.wav: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void pushNotify(Module module, boolean state) {
        notifications.add(new Notification(module, state));
    }

    public void render(MatrixStack matrixStack) {
        for (int counter = 0;counter<notifications.size();counter++) {
            Notification notification = notifications.get(counter);

            if ((System.currentTimeMillis() - notification.createTime) > ((notification.lifetime * 1000))) {
                notification.anim = AnimationMath.fast(notification.anim, 0, 10);
            }
            else{
                notification.anim = AnimationMath.fast(notification.anim, 1, 8);
            }

            if ((System.currentTimeMillis() - notification.createTime - 200) > (notification.lifetime * 1000)) {
                this.notifications.remove(notification);
                continue;
            }

            float x = mc.getMainWindow().scaledWidth() - (notification.font.getStringWidth(notification.text)+10+20)*notification.anim + ((1-notification.anim)*20) - 5;
            float y = mc.getMainWindow().scaledHeight() - 5 - 20 - ((counter * 24));

            notification.render(matrixStack, x, y);
        }
    }

    public enum Type {
        On,
        Off,
        Info,
        Staff,
        Saved,
        Loaded
    }

    public class Notification {
        String text;
        Type type;
        String icon;

        float lifetime = 3.50f;
        long createTime = System.currentTimeMillis();

        CustomFont font = Fonts.SF_DISPLAY.get(13);

        public float anim = 0;

        public Notification(String text){
            this.text = text;
            this.icon = "i";
            this.type = Type.Info;
        }

        public Notification(String text, Type type){
            this.text = text;
            this.type = type;
            if(type == Type.Staff) {
                this.icon = "i";
            } else if(type == Type.Saved) {
                this.icon = "s";
            } else if(type == Type.Loaded) {
                this.icon = "l";
            }
        }

        public Notification(Module module, boolean state){
            this.text = module.getName() + " was " + (state ? "enabled" : "disabled");
            this.icon = module.getCategory().icon();
            this.type = state ? Type.On : Type.Off;
        }

        public void render(MatrixStack matrixStack, float x, float y) {
            font = Fonts.SF_DISPLAY.get(13);

            int alpha = (int) (255 * anim);

            int color = ColorUtils.setAlpha(Nebulae.getHandler().themeManager.getColor(0), alpha);
            if (type == Type.Staff || type == Type.Saved || type == Type.Loaded) {
                color = ColorUtils.setAlpha(Nebulae.getHandler().themeManager.getColor(0), alpha);
            } else if (type == Type.Off) {
                color = ColorUtils.rgba(160, 160, 160, (int) (150 * anim));
            }
            float width = font.getStringWidth(text) + 10 + 20;

            RenderSystem.pushMatrix();
            AnimationMath.sizeAnimation(x, y + 20 / 2, Math.min(1, anim*1.25f));

            ClientHandler.drawSexyRect(x, y, width, 20, 5, (type == Type.On || type == Type.Info || type == Type.Staff || type == Type.Saved || type == Type.Loaded));
            Fonts.ESSENCE_ICONS.get(18).drawString(this.icon, x + 6, y + 8.5f, color);
            font.drawString(text, x + 19, y + 9, ColorUtils.rgba(Nebulae.getHandler().styler.clr_main, Nebulae.getHandler().styler.clr_main, Nebulae.getHandler().styler.clr_main, alpha));

            RenderSystem.popMatrix();
        }
    }
}