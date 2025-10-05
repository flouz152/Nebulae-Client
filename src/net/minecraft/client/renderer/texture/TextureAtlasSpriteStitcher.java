package net.minecraft.client.renderer.texture;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.data.VillagerMetadataSection;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;

public class TextureAtlasSpriteStitcher implements IMetadataSectionSerializer<VillagerMetadataSection>
{
// leaked by itskekoff; discord.gg/sk3d S9LvAaVX
    public VillagerMetadataSection deserialize(JsonObject json)
    {
        return new VillagerMetadataSection(VillagerMetadataSection.HatType.func_217821_a(JSONUtils.getString(json, "hat", "none")));
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "villager";
    }
}
