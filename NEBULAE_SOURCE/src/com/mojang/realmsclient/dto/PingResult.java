package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.minecraft.realms.IPersistentSerializable;

public class PingResult extends ValueObject implements IPersistentSerializable
{
// leaked by itskekoff; discord.gg/sk3d H0hTiCEn
    @SerializedName("pingResults")
    public List<RegionPingResult> field_230571_a_ = Lists.newArrayList();
    @SerializedName("worldIds")
    public List<Long> field_230572_b_ = Lists.newArrayList();
}
