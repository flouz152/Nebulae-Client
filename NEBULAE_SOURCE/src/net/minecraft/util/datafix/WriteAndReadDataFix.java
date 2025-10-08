package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public class WriteAndReadDataFix extends DataFix
{
// leaked by itskekoff; discord.gg/sk3d 7Tm3L41E
    private final String name;
    private final TypeReference type;

    public WriteAndReadDataFix(Schema outputSchema, String name, TypeReference type)
    {
        super(outputSchema, true);
        this.name = name;
        this.type = type;
    }

    protected TypeRewriteRule makeRule()
    {
        return this.writeAndRead(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type));
    }
}
