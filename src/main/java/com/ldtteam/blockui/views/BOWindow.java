package com.ldtteam.blockui.views;

import com.ldtteam.blockui.*;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.function.ToDoubleBiFunction;

/**
 * Blockout window, high level root pane.
 */
@Environment(EnvType.CLIENT)
public class BOWindow extends View
{
    /**
     * The default width.
     */
    public static final int DEFAULT_WIDTH = 420;

    /**
     * The default height.
     */
    public static final int DEFAULT_HEIGHT = 240;

    /**
     * The screen of the window.
     */
    protected BOScreen screen;

    /**
     * Defines if the window should pause the game.
     */
    protected boolean windowPausesGame = true;

    /**
     * Defines if the window should have a lightbox.
     */
    protected boolean lightbox = true;

    /**
     * Render using size or attemp to scale to fullscreen.
     */
    protected WindowRenderType windowRenderType = WindowRenderType.OVERSIZED_VANILLA;

    protected ResourceLocation xmlResourceLocation;

    /**
     * Create a window from an xml file.
     *
     * @param resource ResourceLocation to get file from.
     */
    public BOWindow(final ResourceLocation resource)
    {
        this();
        this.xmlResourceLocation = resource;
        Loader.createFromXMLFile(resource, this);
    }

    /**
     * Make default sized window.
     */
    public BOWindow()
    {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Window constructor when there is a fixed Width and Height.
     *
     * @param w Width of the window, in pixels.
     * @param h Height of the window, in pixels.
     */
    public BOWindow(final int w, final int h)
    {
        super();
        width = w;
        height = h;

        screen = new BOScreen(this);
        window = this;
    }

    /**
     * Load the xml parameters.
     *
     * @param params xml parameters.
     */
    public void loadParams(final PaneParams params)
    {
        params.getResource("inherit", r -> Loader.createFromXMLFile(r, this));

        params.applyShorthand("size", Parsers.INT, 2, a -> {
            width = a.get(0);
            height = a.get(1);
        });

        lightbox = params.getBoolean("lightbox", lightbox);
        windowPausesGame = params.getBoolean("pause", windowPausesGame);
        windowRenderType = params.getEnum("type", WindowRenderType.class, windowRenderType);
    }

    @Override
    public void drawSelf(final BOGuiGraphics ms, final double mx, final double my)
    {
        debugging = Screen.hasShiftDown() && Screen.hasAltDown() && Screen.hasControlDown();

        super.drawSelf(ms, mx, my);
    }

    /**
     * Return {@code true} if the 'lightbox' (default dark background) should be displayed.
     *
     * @return {@code true} if the 'lightbox' should be displayed.
     */
    public boolean hasLightbox()
    {
        return lightbox;
    }

    /**
     * Return {@code true} if the game should be paused when the Window is displayed.
     *
     * @return {@code true} if the game should be paused when the Window is displayed.
     */
    public boolean doesWindowPauseGame()
    {
        return windowPausesGame;
    }

    /**
     * @return {@link WindowRenderType}
     */
    public WindowRenderType getRenderType()
    {
        return windowRenderType;
    }

    /**
     * @return xml defining this window
     */
    public ResourceLocation getXmlResourceLocation()
    {
        return xmlResourceLocation;
    }

    /**
     * Open the window.
     */
    public void open()
    {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            mc.submit(() -> mc.setScreen(screen));
        }
    }

    /**
     * Open the window.
     */
    public void openAsLayer()
    {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            mc.submit(() -> {
                screen.setParent(mc.screen);
                mc.setScreen(screen);
            });
        }
        //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> mc.submit(() -> mc.pushGuiLayer(screen)));
    }

    /**
     * Windows wrap a GuiScreen.
     *
     * @return The current GuiScreen.
     */
    public BOScreen getScreen()
    {
        return screen;
    }

    /**
     * Mouse click released handler.
     * <p>
     * Currently does nothing.
     *
     * @param mx Mouse X position
     * @param my Mouse Y position
     */
    public boolean onMouseReleased(final double mx, final double my)
    {
        // Can be overridden
        return false;
    }

    /**
     * Key input handler. Directs keystrokes to focused Pane, or to onUnhandledKeyTyped() if no
     * Pane handles the keystroke.
     * <p>
     * It is advised not to override this method.
     *
     * @param ch  Character of key pressed.
     * @param key Keycode of key pressed.
     * @return {@code true} if the key was handled by a Pane.
     */
    @Override
    public boolean onKeyTyped(final char ch, final int key)
    {
        if (getFocus() != null && getFocus().onKeyTyped(ch, key))
        {
            return true;
        }

        return onUnhandledKeyTyped(ch, key);
    }

    /**
     * Key input handler when a focused pane did not handle the key.
     * <p>
     * Override this to handle key input at the Window level.
     *
     * @param ch  Character of key pressed.
     * @param key Keycode of key pressed.
     */
    public boolean onUnhandledKeyTyped(final int ch, final int key)
    {
        if (key == GLFW.GLFW_KEY_ESCAPE)
        {
            if (getFocus() != null)
            {
                clearFocus();
            }
            else
            {
                close();
            }
            return true;
        }
        return false;
    }

    /**
     * Close the Window.
     */
    public void close()
    {
        if (Minecraft.getInstance().screen != null)
            Minecraft.getInstance().screen.onClose();
        else
            Minecraft.getInstance().setScreen(null);
        //Minecraft.getInstance().popGuiLayer();
    }

    /**
     * Called when the Window is displayed.
     */
    public void onOpened()
    {
        // Can be overridden
    }

    /**
     * Called when the Window is closed.
     */
    public void onClosed()
    {
        // Can be overridden
    }

    /**
     * Defines how gui should be rendered.
     */
    public enum WindowRenderType
    {
        /**
         * upscaling according to minecraft gui scale settings, no downscaling, max gui resolution is 320*240 px, anything above might not be rendered
         */
        VANILLA((mcWindow, window) -> Math.max(mcWindow.getGuiScale(), 1.0d)),
        /**
         * scaling to size of framebuffer with no lower limit, max gui resolution is unlimited
         */
        FULLSCREEN((mcWindow, window) -> {
            final double widthScale = ((double) mcWindow.getScreenWidth()) / window.getWidth();
            final double heightScale = ((double) mcWindow.getScreenHeight()) / window.getHeight();

            return Math.min(widthScale, heightScale);
        }),
        /**
         * scaling to size of framebuffer with lower limit of 320*240 px, max gui resolution is unlimited
         */
        FULLSCREEN_VANILLA((mcWindow, window) -> {
            final double widthScale = Math.max(mcWindow.getScreenWidth(), 320.0d) / window.getWidth();
            final double heightScale = Math.max(mcWindow.getScreenHeight(), 240.0d) / window.getHeight();

            return Math.min(widthScale, heightScale);
        }),
        /**
         * no upscaling, no downscalling, max gui resolution is unlimited
         */
        FIXED((mcWindow, window) -> 1.0d),
        /**
         * no upscaling, downscaling down to 320*240 px according to size of framebuffer, max gui resolution is unlimited
         */
        FIXED_VANILLA((mcWindow, window) -> Math.min(FULLSCREEN_VANILLA.calcRenderScale(mcWindow, window), 1.0d)),
        /**
         * integer upscaling up to mc gui scale, downscaling down to size of framebuffer, max gui resolution is unlimited
         */
        OVERSIZED((mcWindow, window) -> {
            final double fs = FULLSCREEN.calcRenderScale(mcWindow, window);
            final int userScale = window.mc.options.guiScale().get();
            return fs < 1.0d ? fs : Math.min(Math.floor(fs), userScale == 0 ? Double.MAX_VALUE : userScale);
        }),
        /**
         * integer upscaling up to mc gui scale, downscaling down to 320*240 px according to size of framebuffer, max gui resolution is unlimited
         */
        OVERSIZED_VANILLA((mcWindow, window) -> {
            final double fs_vanilla = FULLSCREEN_VANILLA.calcRenderScale(mcWindow, window);
            final int userScale = window.mc.options.guiScale().get();
            return fs_vanilla < 1.0d ? fs_vanilla : Math.min(Math.floor(fs_vanilla), userScale == 0 ? Double.MAX_VALUE : userScale);
        });

        private final ToDoubleBiFunction<Window, BOWindow> renderScaleCalculator;

        private WindowRenderType(final ToDoubleBiFunction<Window, BOWindow> renderScaleCalculator)
        {
            this.renderScaleCalculator = renderScaleCalculator;
        }

        public double calcRenderScale(final Window mcWindow, final BOWindow window)
        {
            return renderScaleCalculator.applyAsDouble(mcWindow, window);
        }
    }
}
