package com.ldtteam.blockui.mod;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonVanilla;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.fabric.Phase;
import com.ldtteam.blockui.hooks.HookManager;
import com.ldtteam.blockui.hooks.HookRegistries;
import com.ldtteam.blockui.mod.container.ContainerHook;
import com.ldtteam.blockui.util.resloc.OutOfJarResourceLocation;
import com.ldtteam.blockui.views.BOWindow;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.fabricators_of_create.porting_lib.event.client.MouseInputEvents;
import io.github.fabricators_of_create.porting_lib.event.client.OverlayRenderCallback;
import io.github.fabricators_of_create.porting_lib.event.common.TagsUpdatedCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.function.Consumer;

public class ClientEventSubscriber
{
    /**
     * Used to catch the renderWorldLastEvent in order to draw the debug nodes for pathfinding.
     *
     */
    /* TODO: fixme
    public static void renderWorldLastEvent(@NotNull final RenderLevelLastEvent event)
    {
        final PoseStack ps = event.getPoseStack();
        final Vec3 viewPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        ps.pushPose();
        ps.translate(-viewPosition.x(), -viewPosition.y(), -viewPosition.z());
        HookRegistries.render(ps, event.getPartialTick());
        ps.popPose();
    }*/

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            onClientTickEvent(Phase.START);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            onClientTickEvent(Phase.END);
        });

        MouseInputEvents.BEFORE_SCROLL.register((deltaX, deltaY) -> {
            return HookManager.onScroll(deltaY);
        });

        TagsUpdatedCallback.EVENT.register(registryAccess -> {
            onTagsUpdated();
        });

        OverlayRenderCallback.EVENT.register((guiGraphics, partialTicks, window, type) -> {
            return renderOverlay(type);
        });
    }

    /**
     * Used to catch the clientTickEvent.
     * Call renderer cache cleaning every 5 secs (100 ticks).
     *
     * @param phase the current phase
     */
    public static void onClientTickEvent(final Phase phase)
    {
        if (phase == Phase.START && Screen.hasAltDown() && Screen.hasControlDown() && Screen.hasShiftDown())
        {
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_X))
            {
                final BOWindow window = new BOWindow();
                window.addChild(createTestGuiButton(0, "General All-in-one", new ResourceLocation(BlockUI.MOD_ID, "gui/test.xml"), parent -> {
                    parent.findPaneOfTypeByID("missing_out_of_jar", Image.class).setImage(OutOfJarResourceLocation.ofMinecraftFolder(BlockUI.MOD_ID, "missing_out_of_jar.png"), false);
                    parent.findPaneOfTypeByID("working_out_of_jar", Image.class).setImage(OutOfJarResourceLocation.of(BlockUI.MOD_ID, Path.of("../../src/test/resources/button.png")), false);
                }));
                window.addChild(createTestGuiButton(1, "Tooltip Positioning", new ResourceLocation(BlockUI.MOD_ID, "gui/test2.xml")));
                window.addChild(createTestGuiButton(2, "ItemIcon To BlockState", new ResourceLocation(BlockUI.MOD_ID, "gui/test3.xml"), BlockStateTestGui::setup));
                window.addChild(createTestGuiButton(3, "Scrolling Lists", new ResourceLocation(BlockUI.MOD_ID, "gui/test4.xml"), ScrollingListsGui::setup));
                window.open();
            }
        }

        if (phase == Phase.END && Minecraft.getInstance().level != null)
        {
            Minecraft.getInstance().getProfiler().push("hook_manager_tick");
            HookRegistries.tick(Minecraft.getInstance().level.getGameTime());
            Minecraft.getInstance().getProfiler().pop();
        }
    }

    @SafeVarargs
    private static Button createTestGuiButton(final int order,
        final String name,
        final ResourceLocation testGuiResLoc,
        final Consumer<BOWindow>... setups)
    {
        final Button button = new ButtonVanilla();
        button.setPosition((order % 2) * (button.getWidth() + 20), (order / 2) * (button.getHeight() + 10));
        button.setText(Component.literal(name));
        button.setHandler(b -> {
            new BOWindow(testGuiResLoc)
            {
                @Override
                public void onOpened()
                {
                    super.onOpened();
                    for (final Consumer<BOWindow> setup : setups)
                    {
                        setup.accept(this);
                    }
                }
            }.openAsLayer();
        });
        return button;
    }

    /**
     * Used to catch the scroll when no gui is open.
     *
     * @param event the catched event.
     */
    /*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseScrollEvent(final MouseScrollingEvent event)
    {
        // cancel in-game scrolling when raytraced gui has scrolling list
        event.setCanceled(HookManager.onScroll(event.getScrollDelta()));
    }*/

    /**
     * Hook test container gui.
     */
    public static void onTagsUpdated()
    {
        ContainerHook.init();
    }

    public static boolean renderOverlay(OverlayRenderCallback.Types overlay)
    {
        if (Minecraft.getInstance().screen instanceof BOScreen && overlay == OverlayRenderCallback.Types.CROSSHAIRS)
        {
            return true;
        }

        return false;
    }

    /*@SubscribeEvent
    public static void onModMismatch(final ModMismatchEvent event)
    {
        // there are no world data and rest is mod compat anyway
        event.markResolved(BlockUI.MOD_ID);
    }*/
}
