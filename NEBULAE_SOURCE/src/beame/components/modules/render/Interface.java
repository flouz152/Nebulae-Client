package beame.components.modules.render;

import beame.Nebulae;
import beame.feature.ui.*;
import beame.util.animation.AnimationMath;
import events.Event;
import events.impl.player.EventUpdate;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.SliderSetting;

public class Interface extends Module {
// leaked by itskekoff; discord.gg/sk3d MeWcrm3J
    public float enabledAnimation = 0;

    public Interface() {
        super("HUD", Category.Visuals, true, "Включает интерфейса клиента");
        addSettings(widgets, theme, rounding, disableBlur, old, ghost, hideCoordinates);
    }

    public final EnumSetting widgets = new EnumSetting("Элементы",
            new BooleanSetting("Кейбинды", true, 0),
            new BooleanSetting("Эффекты", true, 0),
            new BooleanSetting("Персонал", false, 0),
            new BooleanSetting("Информация", true, 0),
            new BooleanSetting("Таргет", true, 0),
            new BooleanSetting("Броня", true, 0),
            new BooleanSetting("Хотбар", true, 0),
            new BooleanSetting("Задержка", true, 0)
    );
    private final RadioSetting theme = new RadioSetting("Тема", "Основная", "Основная", "Сине-белая", "Кровавая", "Океан",
            "Мегафон", "Ночь", "Огонь", "Нурсултанчик", "Розовая", "Кремень", "Токсичная", "Жёлто-белая", "Жвачка", "Зелёная", "Циановая", "Красно-розовая", "Фиолетовая","УльтраМариновая");
    public final BooleanSetting ghost = new BooleanSetting("Лого в ватермарке", false, 0);
    public final BooleanSetting old = new BooleanSetting("Старая ватермарка", false, 0);
    private final BooleanSetting hideCoordinates = new BooleanSetting("Скрывать координаты", false, 0);
    public final BooleanSetting disableBlur = new BooleanSetting("Отключить блюр", true, 0);

    public final SliderSetting rounding = new SliderSetting("Скругление углов", 4.5f, 1f, 6f, 0.5f);

    public TargetHudDraw th = new TargetHudDraw();
    public WatermarkDraw watermark = new WatermarkDraw();
    public WaterMarkDrawOLD watermarkOLD = new WaterMarkDrawOLD();
    public CooldownsDraw cooldownsDraw = new CooldownsDraw();

    public KeybindsDraw keybinds = new KeybindsDraw();
    public PotionsDraw potions = new PotionsDraw();
    public StaffListDraw stafflist = new StaffListDraw();
    public InfoDraw info = new InfoDraw();
    public ArmorDraw armor = new ArmorDraw();

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            Nebulae.getHandler().themeManager.selectTheme(theme.getIndex());
            if(widgets.get(2).get()) {
                stafflist.update(eventUpdate);
            }
            enabledAnimation = AnimationMath.fast(enabledAnimation, isState() ? 1 : 0, 12);
        }
        if (Nebulae.getHandler().getModuleList().hud.old.get()) {
            Nebulae.getHandler().getModuleList().hud.ghost.setVisible(() -> false);
            Nebulae.getHandler().getModuleList().hud.ghost.set(false);
        } else {
            Nebulae.getHandler().getModuleList().hud.ghost.setVisible(() -> true);
        }

        if (event instanceof Render2DEvent render2DEvent) {
           if (Nebulae.getHandler().getModuleList().hud.old.get()){
               watermarkOLD.render(ghost.get());
           }
           else if (!Nebulae.getHandler().getModuleList().hud.old.get()){
               watermark.render(ghost.get());
           }

           if(widgets.get(0).get()) keybinds.render();
           if(widgets.get(1).get()) potions.render(render2DEvent);
           if(widgets.get(2).get()) stafflist.render(render2DEvent);
           if(widgets.get(3).get()) info.render(hideCoordinates.get());
           if(widgets.get(4).get()) th.render();
           if(widgets.get(5).get()) armor.render(render2DEvent.getMatrix());
           if(widgets.get(7).get()) cooldownsDraw.render(render2DEvent);
        }
    }
}
