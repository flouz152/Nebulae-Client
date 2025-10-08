package beame.util.fonts;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Fonts {
// leaked by itskekoff; discord.gg/sk3d NbRerAvB
    LOGO("ghost.ttf"),
    UI_ICONS("ui_icons.ttf"),
    ICONS2("icons2.ttf"),
    ICONS("icons.ttf"),
    SF_BOLD("main-bold.otf"),
    SF_DISPLAY("main-bold.otf"),
    ESSENCE_ICONS("essence.ttf"),
    SMALLEST("smallestpixel.ttf"),
    SFLIGHT("sflight.otf"),
    SFMEDIUM("sfmedium.otf"),
    SFREGULAR("sfregular.otf"),
    SUISSEINTL("suisseintl.ttf");

    private final String file;
    private final Float2ObjectMap<CustomFont> fontMap = new Float2ObjectArrayMap<>();

    public CustomFont get(float size) {
        return fontMap.computeIfAbsent(size, font -> {
            try {
                return CustomFont.create(getFile(), size, false, false, false);
            } catch (Exception e) {
                throw new RuntimeException("Unable to load font: " + this, e);
            }
        });
    }
}
