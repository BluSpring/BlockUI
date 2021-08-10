package com.ldtteam.blockui.views;

import com.ldtteam.blockui.Color;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.Render;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Simple box element.
 */
public class Box extends View
{
    private float lineWidth = 1.0F;
    private int color = 0xff000000;

    /**
     * Required default constructor.
     */
    public Box()
    {
        super();
    }

    /**
     * Loads box from xml.
     *
     * @param params xml parameters.
     */
    public Box(final PaneParams params)
    {
        super(params);
        lineWidth = params.getFloat("linewidth", lineWidth);
        color = params.getColor("color", color);
    }

    /**
     * Set the color of the box.
     * @param red the red.
     * @param green the green.
     * @param blue the blue.
     */
    public void setColor(final int red, final int green, final int blue)
    {
        this.color = Color.rgbaToInt(red, green, blue, 255);
    }

    /**
     * Setter for the line width property.
     * @param lineWidth
     */
    public void setLineWidth(final float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public void drawSelf(final PoseStack ms, final double mx, final double my)
    {
        Render.drawOutlineRect(ms, x, y, getWidth(), getHeight(), color, lineWidth);

        super.drawSelf(ms, mx, my);
    }

    @Override
    public void drawSelfLast(final PoseStack ms, final double mx, final double my)
    {
        super.drawSelfLast(ms, mx, my);
    }
}