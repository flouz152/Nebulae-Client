package beame.module;

import lombok.Getter;

@Getter
public enum Category {
// leaked by itskekoff; discord.gg/sk3d WIUEYV9t
    Combat(true, "a", 1),
    Player(true, "b", 0),
    Movement(true, "c", 0),
    Visuals(true, "d", 0),
    Misc(true, "e", 0);

    private boolean visible;
    private String icon2;
    public float animation;

    Category(boolean visible, String icon, float animation) { this.visible = visible; this.icon2 = icon; this.animation = animation; }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public String icon() { return this.icon2; }

    public static void setAllCategoriesVisible(boolean visible) {
        for (Category category : Category.values()) {
            category.setVisible(visible);
        }
    }
}
