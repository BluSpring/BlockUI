package com.ldtteam.blockui.views;

import com.ldtteam.blockui.MouseEventCallback;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.util.records.Pos2i;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Basic scrollable pane.
 */
public class ScrollingContainer extends View
{
    private static final int PERCENT_90 = 90;
    private static final int PERCENT_FULL = 100;

    protected ScrollingView owner;
    protected double scrollY = 0;
    protected int contentHeight = 0;

    ScrollingContainer(final ScrollingView owner)
    {
        super();
        this.owner = owner;
    }

    @Override
    public void parseChildren(final PaneParams params)
    {
        super.parseChildren(params);
        computeContentHeight();
    }

    /**
     * Compute the height in pixels of the container.
     */
    public void computeContentHeight()
    {
        contentHeight = 0;

        for (final Pane child : children)
        {
            if (child != null)
            {
                contentHeight = Math.max(contentHeight, child.getY() + child.getHeight());
            }
        }

        // Recompute scroll
        setScrollY(scrollY);
    }

    /**
     * Compute the height in pixels of the container.
     */
    public void setContentHeight(final int size)
    {
        contentHeight = size;
        // Recompute scroll
        setScrollY(scrollY);
    }

    public int getMaxScrollY()
    {
        return Math.max(0, contentHeight - getHeight());
    }

    @Override
    public void drawSelf(final PoseStack ms, final double mx, final double my)
    {
        scissorsStart(ms, width, contentHeight);

        // Translate the scroll
        ms.pushPose();
        ms.translate(0.0d, -scrollY, 0.0d);
        super.drawSelf(ms, mx, my + scrollY);
        ms.popPose();

        scissorsEnd(ms);
    }

    @Override
    public void drawSelfLast(final PoseStack ms, final double mx, final double my)
    {
        // Translate the scroll
        ms.pushPose();
        ms.translate(0.0d, -scrollY, 0.0d);
        super.drawSelfLast(ms, mx, my + scrollY);
        ms.popPose();
    }

    @Override
    protected boolean childIsVisible(final Pane child)
    {
        return child.getX() < getWidth() && child.getY() < getHeight() + scrollY && (child.getX() + child.getWidth()) >= 0 &&
            (child.getY() + child.getHeight()) >= scrollY;
    }

    public double getScrollY()
    {
        return scrollY;
    }

    /**
     * @param scroll new scroll value [pixels]
     * @return true if scroll offset changed
     */
    public boolean setScrollY(final double scroll)
    {
        final double oldScroll = scrollY;
        final double maxScrollY = getMaxScrollY();

        scrollY = scroll;
        if (scrollY > maxScrollY)
        {
            scrollY = maxScrollY;
        }
        else if (scrollY < 0)
        {
            scrollY = 0;
        }
        return oldScroll != scrollY;
    }

    public int getContentHeight()
    {
        return contentHeight;
    }

    public int getScrollPageSize()
    {
        return getHeight() * PERCENT_90 / PERCENT_FULL;
    }

    /**
     * Scroll down a certain amount of pixels.
     *
     * @param deltaY number of pixels to scroll.
     */
    public void scrollBy(final double deltaY)
    {
        setScrollY(scrollY + deltaY);
    }

    protected Pos2i.MutablePos2i accumulatePosition(final Pos2i.MutablePos2i accumulator)
    {
        if (parent != null)
        {
            parent.accumulatePosition(accumulator);
        }
        accumulator.x += x;
        accumulator.y += y - scrollY;
        return accumulator;
    }


    @Override
    public boolean mouseEventProcessor(final double mx,
        final double my,
        final MouseEventCallback panePredicate,
        final MouseEventCallback eventCallbackPositive,
        final MouseEventCallback eventCallbackNegative)
    {
        return super.mouseEventProcessor(mx, my + scrollY, panePredicate, eventCallbackPositive, eventCallbackNegative);
    }
}
