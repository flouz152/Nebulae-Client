package net.minecraft.client.resources.data;

import com.google.gson.JsonObject;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;

public class TextureMetadataSectionSerializer implements IMetadataSectionSerializer<TextureMetadataSection>
{
// leaked by itskekoff; discord.gg/sk3d bmhQ4xNh
    public TextureMetadataSection deserialize(JsonObject json)
    {
        boolean flag = JSONUtils.getBoolean(json, "blur", false);
        boolean flag1 = JSONUtils.getBoolean(json, "clamp", false);
        return new TextureMetadataSection(flag, flag1);
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "texture";
    }
}
