package com.ldtteam.blockui.mod;

import com.ldtteam.blockui.Loader;
import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Blocks;

public class ClientLifecycleSubscriber
{
    public static void init() {
        onRegisterReloadListeners(ResourceManagerHelper.get(PackType.CLIENT_RESOURCES));
        ColorHandlersCallback.BLOCK.register(ClientLifecycleSubscriber::onRegisterBlockColor);
    }

    public static void onRegisterReloadListeners(final ResourceManagerHelper event)
    {
        event.registerReloadListener(Loader.INSTANCE);
    }

    public static void onRegisterBlockColor(final BlockColors event)
    {
        // replace cauldron with plains default color (4159204, with slighty more light in HSL += 8%)
        event.register(
            (state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : 0x638fe9,
            Blocks.WATER_CAULDRON);
    }
}
