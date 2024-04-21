package com.ldtteam.blockui.fabric;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class FakePlayerFactory {
    private static GameProfile MINECRAFT = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), "[Minecraft]");
    private static WeakReference<FakePlayer> MINECRAFT_PLAYER = null;

    public static FakePlayer getMinecraft(ServerLevel level) {
        var ret = MINECRAFT_PLAYER != null ? MINECRAFT_PLAYER.get() : null;
        if (ret == null) {
            ret = FakePlayer.get(level, MINECRAFT);
            MINECRAFT_PLAYER = new WeakReference<>(ret);
        }

        return ret;
    }
}

