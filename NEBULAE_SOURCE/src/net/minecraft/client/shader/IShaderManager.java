package net.minecraft.client.shader;

public interface IShaderManager
{
// leaked by itskekoff; discord.gg/sk3d d29kWg24
    int getProgram();

    void markDirty();

    ShaderLoader getVertexShaderLoader();

    ShaderLoader getFragmentShaderLoader();
}
