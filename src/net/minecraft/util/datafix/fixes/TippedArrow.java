package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;

public class TippedArrow extends TypedEntityRenameHelper
{
// leaked by itskekoff; discord.gg/sk3d d4aBWw4v
    public TippedArrow(Schema outputSchema, boolean changesType)
    {
        super("EntityTippedArrowFix", outputSchema, changesType);
    }

    protected String rename(String name)
    {
        return Objects.equals(name, "TippedArrow") ? "Arrow" : name;
    }
}
