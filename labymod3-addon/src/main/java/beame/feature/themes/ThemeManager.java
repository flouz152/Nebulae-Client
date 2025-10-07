package beame.feature.themes;

import beame.util.color.ColorUtils;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThemeManager {
// leaked by itskekoff; discord.gg/sk3d XltJjGqm
    public int currentTheme = 0;
    public ArrayList<Theme> themeList = new ArrayList<>();

    public ThemeManager() {
        initialize();
    }

    private void initialize() {
        themeList.addAll(Arrays.asList(
                new Theme("Main", new Color(108, 101, 204), new Color(116, 100, 190)),
                new Theme("Blue-white", new Color(152, 245, 249), new Color(226, 234, 244)),
                new Theme("Bloody", new Color(210, 30, 45), new Color(113, 19, 19)),
                new Theme("Ocean", new Color(79, 109, 213), new Color(70, 99, 159)),
                new Theme("Megaphone", new Color(98, 170, 39), new Color(118, 64, 221)),
                new Theme("Essence", new Color(89, 76, 211), new Color(36, 20, 76)),
                new Theme("Fire", new Color(251, 210, 71), new Color(227, 142, 63)),
                new Theme("Blue combo", new Color(0, 88, 255), new Color(123, 191, 236)),
                new Theme("Cute ^_^", new Color(230, 137, 189), new Color(234, 225, 225)),
                new Theme("Flint", new Color(113, 113, 113), new Color(200, 198, 198)),
                new Theme("Toxic", new Color(103, 228, 74), new Color(88, 165, 63)),
                new Theme("Yelo-white", new Color(255, 224, 66), new Color(255, 255, 255)),
                new Theme("Bubble gum", new Color(244, 179, 243), new Color(90, 180, 213)),
                new Theme("Mojito", new Color(11, 59, 42), new Color(58, 98, 73)),
                new Theme("Cean", new Color(39, 207, 164), new Color(105, 255, 255)),
                new Theme("RedAndPink", new Color(192, 106, 199), new Color(199, 106, 113)),
                new Theme("Purple", new Color(133, 23, 250), new Color(69, 21, 178)),
                new Theme("UltraMarin", new Color(18,10,143), new Color(27,21,126))
        ));
    }


    public void selectTheme(int themeNum){
        currentTheme = themeNum;
    }

    private Color getThemeColorNonRGB(int num) {
        Color[] colors = { themeList.get(currentTheme).getColors()[0], themeList.get(currentTheme).getColors()[1] };
        return colors[num];
    }



    public int getThemeColor(int num) { return getThemeColorNonRGB(num).getRGB(); }

    public int getColor(int num) {
        Color color1 = getThemeColorNonRGB(0);
        Color color2 = getThemeColorNonRGB(1);
        return ColorUtils.interpolateTwoColors(15, num, color1, color2, false).getRGB();
    }
}
