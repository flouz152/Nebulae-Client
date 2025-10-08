package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

public class Splashes extends ReloadListener<List<String>>
{
// leaked by itskekoff; discord.gg/sk3d ONHMVmwm
    private static final ResourceLocation SPLASHES_LOCATION = new ResourceLocation("texts/splashes.txt");
    private static final Random RANDOM = new Random();
    private final List<String> possibleSplashes = Lists.newArrayList();
    private final Session gameSession;

    public Splashes(Session gameSessionIn)
    {
        this.gameSession = gameSessionIn;
    }

    protected List<String> prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        try (
                IResource iresource = Minecraft.getInstance().getResourceManager().getResource(SPLASHES_LOCATION);
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
            )
        {
            return bufferedreader.lines().map(String::trim).filter((p_215277_0_) ->
            {
                return p_215277_0_.hashCode() != 125780783;
            }).collect(Collectors.toList());
        }
        catch (IOException ioexception)
        {
            return Collections.emptyList();
        }
    }

    protected void apply(List<String> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        this.possibleSplashes.clear();
        this.possibleSplashes.addAll(objectIn);
    }

    @Nullable
    public String getSplashText()
    {
        return "1488";
    }
}
