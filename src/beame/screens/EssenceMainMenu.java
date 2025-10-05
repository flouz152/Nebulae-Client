package beame.screens;

import beame.Essence;
import beame.managers.alts.AltManager;
import beame.screens.api.EssenceButton;
import beame.screens.api.EssenceMenuStorage;
import beame.screens.api.EssenceTextBox;
import beame.util.Scissor;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.math.TimerUtil;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import static beame.util.IMinecraft.mc;

public class EssenceMainMenu extends Screen {
// leaked by itskekoff; discord.gg/sk3d sI69nwmt

    public EssenceMainMenu() {
        super(ITextComponent.getTextComponentOrEmpty("ESSENCEMAINMENU"));
    }

    public EssenceMainMenu(boolean emptyBoolean) {
        super(ITextComponent.getTextComponentOrEmpty("ESSENCEMAINMENU"));
    }

    public EssenceMainMenu(boolean emptyBoolean, boolean isAltManager) {
        super(ITextComponent.getTextComponentOrEmpty("ESSENCEMAINMENU"));
        EssenceMenuStorage.isAltManager = isAltManager;
    }

    public List<EssenceButton> ebuttons = new ArrayList<>();

    @Override
    protected void init() {
        ebuttons.clear();
        super.init();
        // Если открыли AltManager, и выбранный ник есть — делаем его текущим
        if (EssenceMenuStorage.isAltManager) {
            String selected = Essence.getHandler().altManager.getSelectedNickname();
            if (selected != null && !selected.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                mc.session = new Session(selected, uuid, "", "mojang");
            }
        }
    }

    EssenceTextBox textBoxInput;

    float scroll = 0;

    @Override
    public void tick() {
        int buttonWidth = (75);
        int buttonHeight = (20);
        int centredX = (mc.getMainWindow().getScaledWidth() / 2) - buttonWidth / 2;
        int centredY = (mc.getMainWindow().getScaledHeight() / 2) - buttonHeight / 2;

        if(ebuttons.isEmpty()) {
            if(EssenceMenuStorage.isAltManager){
                textBoxInput = new EssenceTextBox(this.font, (int)(centredX - buttonWidth / 2 - 2), (int)(centredY), 75, 18, new StringTextComponent(""));
                addButton(textBoxInput);
                ebuttons.addAll(List.of(
                        new EssenceButton("Random".toUpperCase(), (centredX - (buttonWidth / 2) - 2), centredY+42, buttonWidth, 20, (input) -> {
                            String[] words = { "Esau", "esbat", "esbats", "esca", "escabeche", "escabeches", "escadrille", "escadrilles", "escalade", "escaladed", "escalader", "escaladers", "escalades", "escalading", "escalado", "escalados", "escalate", "escalated", "escalates", "escalating", "escalation", "escalations", "escalator", "escalators", "escalatory", "escallonia", "Escalloniaceae", "escalloniaceous", "escallonias", "escallop", "escallopine", "escallopines", "escallops", "escalope", "escalopes", "escambio", "escambios", "escambron", "escambrons", "escamotage", "escamotages", "escapable", "escapade", "escapades", "escape", "escaped", "escapee", "escapees", "escapeless", "escapement", "escapements", "escaper", "escapers", "escapes", "escapeway", "escapeways", "escaping", "escapingly", "escapism", "escapisms", "escapist", "escapists", "escapologies", "escapologist", "escrod", "escrol", "escrolls", "escrols", "escrow", "escrowed", "escrowee", "escrowing", "escrows", "escuage", "escuages", "escudo", "escudos", "esculent", "esculents", "esculetin", "esculin", "escutcheon", "escutcheoned", "escutcheons", "escutellate", "esdragol", "esdragols", "Esdras", "esemplastic", "eseptate", "eserine", "eserines", "eserinise", "eserinised", "eserinising", "eserinize", "eserinized", "eserinizing", "eseroline", "eserolines", "eshin", "eshins", "esill", "esills", "esiphonal", "esiphonate", "esker", "eskers", "Eskimo", "chipsina","zchipsina","chipsina_top","NOTchipsina","fakEchipsina","chipsina_penit","chipsina_bust","chipsinabustit","cfGchipsini","YTchipsina","chipsinaYT","MyYTchipsina","BIGchipsik"};
                            String word = words[new Random().nextInt(0, words.length-1)];
                            String nickname = word + System.currentTimeMillis() % 1000;
                            Essence.getHandler().altManager.add(nickname);
                            Essence.getHandler().altManager.setSelectedNickname(nickname);
                            String uuid = UUID.randomUUID().toString();
                            mc.session = new Session(nickname, uuid, "", "mojang");
                            assert this.minecraft != null;
                            this.minecraft.displayGuiScreen(new EssenceMainMenu(false, true));
                        }),
                        new EssenceButton("Back".toUpperCase(), (centredX - (buttonWidth / 2) - 2), centredY+64, buttonWidth, 20, (input) -> {
                            assert this.minecraft != null;
                            this.minecraft.displayGuiScreen(new EssenceMainMenu(false, false));
                        }),
                        new EssenceButton("Add".toUpperCase(), (centredX - (buttonWidth / 2) - 2), centredY + 20, buttonWidth, buttonHeight, (input) -> {
                            if(textBoxInput.text.isEmpty() || textBoxInput.text.contains(" ") || Pattern.matches(".*\\p{InCyrillic}.*", textBoxInput.text)){
                                return;
                            }
                            Essence.getHandler().altManager.add(textBoxInput.text);
                            Essence.getHandler().altManager.setSelectedNickname(textBoxInput.text);
                            String uuid = UUID.randomUUID().toString();
                            mc.session = new Session(textBoxInput.text, uuid, "", "mojang");
                            assert this.minecraft != null;
                            this.minecraft.displayGuiScreen(new EssenceMainMenu(false, true));
                        })
                ));
            } else {
                assert this.minecraft != null;
                ebuttons.addAll(List.of(
                        new EssenceButton("Singleplayer".toUpperCase(), (centredX - (buttonWidth / 2) - 2), centredY, buttonWidth, buttonHeight, (input) -> {
                            this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
                        }),
                        new EssenceButton("Multiplayer".toUpperCase(), (centredX + (buttonWidth / 2) + 2), centredY, buttonWidth, buttonHeight, (input) -> {
                            this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
                        }),
                        new EssenceButton("Options".toUpperCase(), (centredX - (buttonWidth / 2) - 2), centredY + buttonHeight + 4, buttonWidth, buttonHeight, (input) -> {
                            this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
                        }),
                        new EssenceButton("AltManager".toUpperCase(), (centredX + (buttonWidth / 2) + 2), centredY + buttonHeight + 4, buttonWidth, buttonHeight, (input) -> {
                            this.minecraft.displayGuiScreen(new EssenceMainMenu(false, true));
                        }),
                        new EssenceButton("Exit".toUpperCase(), centredX - buttonWidth / 2 - 2, centredY + buttonHeight * 2 + 4 * 2, buttonWidth * 2 + 4, buttonHeight, (input) -> {
                            mc.close();
                            System.exit(0);
                        })
                ));
            }
        }

        if(textBoxInput != null) textBoxInput.tick();
        super.tick();
    }

    public TimerUtil bgAnimator = new TimerUtil();

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(bgAnimator.hasTimeElapsed(20)){
            EssenceMenuStorage.bgAnim += 0.05f;
            bgAnimator.reset();
        }

        ClientHandler.drawMenuBackground(-5, -5, mc.getMainWindow().getWidth() + 10, mc.getMainWindow().getHeight() + 10, Essence.getHandler().themeManager.getColor(0), Essence.getHandler().themeManager.getColor(0), EssenceMenuStorage.bgAnim);
        ClientHandler.drawRound(-5, -5, mc.getMainWindow().getWidth() + 10, mc.getMainWindow().getHeight() + 10, 0, ColorUtils.getColor(0, 0, 0, 60));

        String text = "Essence";
        int centredX = (int) ((mc.getMainWindow().getScaledWidth() / 2));
        int centredY = (int) ((mc.getMainWindow().getScaledHeight() / 2) - 40);
//        Fonts.LOGO.get(55).drawCenteredString(matrixStack, "Z", centredX, centredY - 33, Essence.getHandler().themeManager.getColor(180));
        Fonts.SF_BOLD.get(24).drawCenteredString(matrixStack, text, centredX, centredY + 4, -1);
        Fonts.SUISSEINTL.get(14).drawCenteredString(matrixStack, "essencepenit.fun", centredX, mc.getMainWindow().getScaledHeight() - 10, ColorUtils.rgba(255, 255, 255, 100));

        ClientHandler.drawRound(centredX - (float) 160 /2 - 2, centredY + 26 - 2, 162 + 4, 75 + 1.5f + 4 + (EssenceMenuStorage.isAltManager ? 14 : 0), 6, ColorUtils.getColor(20, 20, 20, 80));
        for(EssenceButton button : ebuttons){
            button.render(matrixStack);
            button.hover = ClientHandler.isInRegion((int) mouseX, (int) mouseY, button.x, button.y, button.width, button.height);
        }

        if(EssenceMenuStorage.isAltManager) {
            ClientHandler.drawRound(centredX + 3, centredY + 30, 75, 84, 3, ColorUtils.getColor(20, 20, 20, 50));
            Fonts.SUISSEINTL.get(14).drawCenteredString(matrixStack, "Текущий аккаунт: " + mc.getSession().getUsername(), centredX, centredY + 130, ColorUtils.rgba(255, 255, 255, 100));

            if(!Essence.getHandler().altManager.accounts.isEmpty()) {
                int index = 0;

                Scissor.push();
                Scissor.setFromComponentCoordinates(centredX + 3, centredY + 32, 72, 82);
                for (AltManager.AltAccount alt : Essence.getHandler().altManager.accounts) {
                    ClientHandler.drawRound((centredX + 8), centredY + scroll + 35 + (16 * index), 75 - 10, 14, 3, ColorUtils.getColor(20, 20, 20, 50));
                    Fonts.SUISSEINTL.get(13).drawString(matrixStack, alt.nickname, (centredX + 8 + 4), centredY + scroll + 35 + (16 * index) + 5.5f, -1);

                    index += 1;
                }
                Scissor.unset();
                Scissor.pop();
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(textBoxInput != null) {
            textBoxInput.mouseClicked(mouseX, mouseY, button);
        }
        if(EssenceMenuStorage.isAltManager) {
            if (!Essence.getHandler().altManager.accounts.isEmpty()) {
                int index = 0;

                int centredX = (int) ((mc.getMainWindow().getScaledWidth() / 2));
                int centredY = (int) ((mc.getMainWindow().getScaledHeight() / 2) - 40);
                for (AltManager.AltAccount alt : Essence.getHandler().altManager.accounts) {
                    if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, (centredX + 8), (int) (centredY + scroll + 35 + (16 * index)), 75 - 10, 14)) {
                        if (button == 0) {
                            String nickname = alt.nickname;
                            if (nickname.length() >= 3 && nickname.length() <= 16) {
                                String uuid = UUID.randomUUID().toString();
                                mc.session = new Session(nickname, uuid, "", "mojang");
                                Essence.getHandler().altManager.setSelectedNickname(nickname);
                            }
                        } else {
                            Essence.getHandler().altManager.accounts.remove(index);
                            // Если удалили выбранный ник, сбрасываем выбор
                            if (Essence.getHandler().altManager.selectedNickname != null && Essence.getHandler().altManager.selectedNickname.equals(alt.nickname)) {
                                Essence.getHandler().altManager.setSelectedNickname(null);
                            }
                        }
                        break;
                    }

                    index += 1;
                }
            }
        }

        for (EssenceButton btn : ebuttons) {
            if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, btn.x, btn.y, btn.width, btn.height)) {
                if (button == 0) {
                    btn.onClick(mouseX, mouseY);
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void closeScreen() {
        if(EssenceMenuStorage.isAltManager) EssenceMenuStorage.isAltManager = false;
        super.closeScreen();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scroll += (float) (delta * 10);
        scroll = MathHelper.clamp(scroll, -(Essence.getHandler().altManager.accounts.size()*20), 0);

        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
