package net.optifine.reflect;

import java.lang.reflect.Field;

public class FieldLocatorFixed implements IFieldLocator
{
// leaked by itskekoff; discord.gg/sk3d QQixksFw
    private Field field;

    public FieldLocatorFixed(Field field)
    {
        this.field = field;
    }

    public Field getField()
    {
        return this.field;
    }
}
