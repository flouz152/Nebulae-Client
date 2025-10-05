package net.optifine.gui;

import java.awt.Rectangle;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;

public interface TooltipProvider
{
// leaked by itskekoff; discord.gg/sk3d OiLuMGCi
    Rectangle getTooltipBounds(Screen var1, int var2, int var3);

    String[] getTooltipLines(Widget var1, int var2);

    boolean isRenderBorder();
}
