package net.minecraft.resources;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourcePackFileNotFoundException extends FileNotFoundException
{
// leaked by itskekoff; discord.gg/sk3d LXbZZnKg
    public ResourcePackFileNotFoundException(File resourcePack, String fileName)
    {
        super(String.format("'%s' in ResourcePack '%s'", fileName, resourcePack));
    }
}
