package beame.feature.themes;

import java.awt.Color;

public class Theme {
// leaked by itskekoff; discord.gg/sk3d Dbg8BSZU
    private String name;
    private Color color1;
    private Color color2;

    private void initialize(String name, Color color1, Color color2) {
        this.name = name;
        this.color1 = color1;
        this.color2 = color2;
    }


    public Theme(String name, Color color1, Color color2){
        initialize(name,
                color1,
                color2
        );
    }

    public String getName() { return name; }
    public Color[] getColors() { Color[] a = { color1, color2 }; return a; }
}
