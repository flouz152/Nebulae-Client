package net.minecraft.client.settings;

public enum PointOfView {
// leaked by itskekoff; discord.gg/sk3d 10z5GUZM
    FIRST_PERSON(true, false),
    THIRD_PERSON_BACK(false, false),
    THIRD_PERSON_FRONT(false, true);

    private static final PointOfView[] field_243189_d = values();
    private boolean field_243190_e;
    private boolean field_243191_f;

    private PointOfView(boolean p_i242049_3_, boolean p_i242049_4_) {
        this.field_243190_e = p_i242049_3_;
        this.field_243191_f = p_i242049_4_;
    }

    public boolean firstPerson() {
        return this.field_243190_e;
    }

    public boolean thirdPersonFront() {
        return this.field_243191_f;
    }

    public PointOfView nextPointOfView() {
        return field_243189_d[(this.ordinal() + 1) % field_243189_d.length];
    }
}
