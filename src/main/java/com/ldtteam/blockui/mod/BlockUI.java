package com.ldtteam.blockui.mod;

import net.fabricmc.api.ClientModInitializer;

public class BlockUI implements ClientModInitializer
{
    public static final String MOD_ID = "blockui";

    public void onInitializeClient()
    {
        ClientLifecycleSubscriber.init();
        ClientEventSubscriber.init();
    }
}
