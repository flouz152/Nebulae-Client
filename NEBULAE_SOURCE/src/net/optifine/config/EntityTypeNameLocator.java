package net.optifine.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.EntityTypeUtils;

public class EntityTypeNameLocator implements IObjectLocator<String>
{
// leaked by itskekoff; discord.gg/sk3d P5bdJyw9
    public String getObject(ResourceLocation loc)
    {
        EntityType entitytype = EntityTypeUtils.getEntityType(loc);
        return entitytype == null ? null : entitytype.getTranslationKey();
    }

    public static String getEntityTypeName(Entity entity)
    {
        return entity.getType().getTranslationKey();
    }
}
