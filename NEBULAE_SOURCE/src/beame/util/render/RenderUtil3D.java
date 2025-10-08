package beame.util.render;

import beame.util.IMinecraft;
import beame.util.math.Interpolator;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

import org.lwjgl.opengl.GL11;

@UtilityClass
public class RenderUtil3D implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d HltFlENG
    public Vector3d interpolate(Entity entity, float partialTicks) {
        double posX = Interpolator.lerp(entity.lastTickPosX, entity.getPosX(), partialTicks);
        double posY = Interpolator.lerp(entity.lastTickPosY, entity.getPosY(), partialTicks);
        double posZ = Interpolator.lerp(entity.lastTickPosZ, entity.getPosZ(), partialTicks);
        return new Vector3d(posX, posY, posZ);
    }

    public void drawFilledBoundingBox(MatrixStack matrix, AxisAlignedBB bb, int color, boolean depth, boolean cull) {
        if (!depth) RenderSystem.disableDepthTest();
        else RenderSystem.depthMask(false);
        if (!cull) RenderSystem.disableCull();
        drawQuadH(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ, color, true, true);
        drawQuadH(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, true, true);
        drawQuad(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ, color, true, true);
        drawQuad(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, true, true);
        drawQuad(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, true, true);
        drawQuad(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ, color, true, true);
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
        else RenderSystem.depthMask(true);
    }

    public void drawBoundingBox(MatrixStack matrix, AxisAlignedBB bb, int color, float lineWidth, boolean depth, boolean cull) {
        if (!depth) RenderSystem.disableDepthTest();
        if (!cull) RenderSystem.disableCull();
        drawLineQuad(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ, color, lineWidth, true, true);
        drawLineQuad(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, lineWidth, true, true);

        drawLine(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.minX, (float) bb.maxY, (float) bb.minZ, color, lineWidth, true, true);
        drawLine(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ, color, lineWidth, true, true);
        drawLine(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, lineWidth, true, true);
        drawLine(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ, color, lineWidth, true, true);
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
    }

    public static void drawBox(AxisAlignedBB bb, int color) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1);
        float[] rgb = RenderUtil.IntColor.rgb(color);
        RenderSystem.color4f(rgb[0], rgb[1], rgb[2], rgb[3]);
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        tessellator.draw();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        tessellator.draw();
        vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        tessellator.draw();
        RenderSystem.color4f(rgb[0], rgb[1], rgb[2], rgb[3]);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }

    private void drawFace(Matrix4f matrix4f, float x1, float y1, float z1, float x2, float y2, float z2, float[] rgb) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(matrix4f, x1, y1, z1).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        bufferBuilder.pos(matrix4f, x2, y1, z1).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        bufferBuilder.pos(matrix4f, x2, y1, z2).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        bufferBuilder.pos(matrix4f, x1, y1, z2).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        bufferBuilder.pos(matrix4f, x1, y1, z1).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        tessellator.draw();
    }

    private void drawEdge(Matrix4f matrix4f, float x1, float y1, float z1, float x2, float y2, float z2, float[] rgb) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(matrix4f, x1, y1, z1).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        bufferBuilder.pos(matrix4f, x2, y2, z2).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        tessellator.draw();
    }

    public void drawQuad(MatrixStack matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color, boolean depth, boolean cull) {
        float dx1 = (float) (x1 - cameraPos().x);
        float dy1 = (float) (y1 - cameraPos().y);
        float dz1 = (float) (z1 - cameraPos().z);

        float dx2 = (float) (x2 - cameraPos().x);
        float dy2 = (float) (y2 - cameraPos().y);
        float dz2 = (float) (z2 - cameraPos().z);

        matrix.push();
        Matrix4f matrix4f = matrix.getLast().getMatrix();

        setupWorldRenderer();

        if (!depth) RenderSystem.disableDepthTest();
        if (!cull) RenderSystem.disableCull();
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(matrix4f, dx1, dy1, dz1).color(color).endVertex();
        bufferBuilder.pos(matrix4f, dx1, dy2, dz1).color(color).endVertex();
        bufferBuilder.pos(matrix4f, dx2, dy2, dz2).color(color).endVertex();
        bufferBuilder.pos(matrix4f, dx2, dy1, dz2).color(color).endVertex();
        tessellator.draw();
        
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
        cleanupWorldRenderer();
        matrix.pop();
    }

    public void drawQuadH(MatrixStack matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color, boolean depth, boolean cull) {
        float dx1 = (float) (x1 - cameraPos().x);
        float dy1 = (float) (y1 - cameraPos().y);
        float dz1 = (float) (z1 - cameraPos().z);

        float dx2 = (float) (x2 - cameraPos().x);
        float dy2 = (float) (y2 - cameraPos().y);
        float dz2 = (float) (z2 - cameraPos().z);

        matrix.push();
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        setupWorldRenderer();
        if (!depth) RenderSystem.disableDepthTest();
        if (!cull) RenderSystem.disableCull();
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(matrix4f, dx1, dy1, dz1).color(color).endVertex();
        bufferBuilder.pos(matrix4f, dx2, dy1, dz1).color(color).endVertex();
        bufferBuilder.pos(matrix4f, dx2, dy2, dz2).color(color).endVertex();
        bufferBuilder.pos(matrix4f, dx1, dy2, dz2).color(color).endVertex();
        tessellator.draw();
        
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
        cleanupWorldRenderer();
        matrix.pop();
    }

    public void drawLineQuad(MatrixStack matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color, float lineWidth, boolean depth, boolean cull) {
        drawLine(matrix, x1, y1, z1, x2, y2, z1, color, lineWidth, depth, cull);
        drawLine(matrix, x2, y1, z1, x2, y2, z2, color, lineWidth, depth, cull);
        drawLine(matrix, x2, y1, z2, x1, y2, z2, color, lineWidth, depth, cull);
        drawLine(matrix, x1, y1, z2, x1, y2, z1, color, lineWidth, depth, cull);
    }

    public void drawLine(MatrixStack matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color, float lineWidth, boolean depth, boolean cull) {
        float dx1 = (float) (x1 - cameraPos().x);
        float dy1 = (float) (y1 - cameraPos().y);
        float dz1 = (float) (z1 - cameraPos().z);

        float dx2 = (float) (x2 - cameraPos().x);
        float dy2 = (float) (y2 - cameraPos().y);
        float dz2 = (float) (z2 - cameraPos().z);

        matrix.push();
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        setupWorldRenderer();
        enableBlendAndSmoothLines(lineWidth);
        if (!depth) RenderSystem.disableDepthTest();
        if (!cull) RenderSystem.disableCull();
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(matrix4f, dx1, dy1, dz1).color(color).endVertex();
        bufferBuilder.pos(matrix4f, dx2, dy2, dz2).color(color).endVertex();
        tessellator.draw();
        
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
        disableBlendAndSmoothLines();
        cleanupWorldRenderer();
        matrix.pop();
    }

    public Vector3d cameraPos() {
        return mc.gameRenderer.getActiveRenderInfo().getProjectedView();
    }

    public void setupOrientationMatrix(MatrixStack matrix, double x, double y, double z) {
        matrix.translate(x - cameraPos().x, y - cameraPos().y, z - cameraPos().z);
    }

    public void rotateToCamera(MatrixStack matrix) {
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
    }

    public void setupWorldRenderer() {
        RenderSystem.disableLighting();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableTexture();
        setupBlend();
    }

    public void cleanupWorldRenderer() {
        cleanupBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
    }

    private void enableBlendAndSmoothLines(float lineWidth) {
        setupBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(lineWidth);
    }

    private void disableBlendAndSmoothLines() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1F);
        cleanupBlend();
    }

    private static void setupBlend() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private static void cleanupBlend() {
        RenderSystem.disableBlend();
    }
}