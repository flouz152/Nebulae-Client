package net.optifine.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public class VertexBuilderDummy implements IVertexBuilder
{
// leaked by itskekoff; discord.gg/sk3d dYzr9yR3
    private IRenderTypeBuffer.Impl renderTypeBuffer = null;

    public VertexBuilderDummy(IRenderTypeBuffer.Impl renderTypeBuffer)
    {
        this.renderTypeBuffer = renderTypeBuffer;
    }

    public IRenderTypeBuffer.Impl getRenderTypeBuffer()
    {
        return this.renderTypeBuffer;
    }

    @Override
    public IVertexBuilder color(int color) {
        return null;
    }

    public IVertexBuilder pos(double x, double y, double z)
    {
        return this;
    }

    public IVertexBuilder color(int red, int green, int blue, int alpha)
    {
        return this;
    }

    public IVertexBuilder tex(float u, float v)
    {
        return this;
    }

    public IVertexBuilder overlay(int u, int v)
    {
        return this;
    }

    public IVertexBuilder lightmap(int u, int v)
    {
        return this;
    }

    public IVertexBuilder normal(float x, float y, float z)
    {
        return this;
    }

    public void endVertex()
    {
    }
}
