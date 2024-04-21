package com.ldtteam.blockui.fabric;

import com.google.common.collect.Maps;
import com.ldtteam.blockui.mixin.ItemPropertiesAccessor;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemPropertiesHelper {
    public static ItemPropertyFunction registerGeneric(ResourceLocation name, ItemPropertyFunction property) {
        ItemPropertiesAccessor.getGenericProperties().put(name, property);
        return property;
    }

    public static void register(Item item, ResourceLocation name, ItemPropertyFunction property) {
        (ItemPropertiesAccessor.getProperties().computeIfAbsent(item, (itemx) -> {
            return Maps.newHashMap();
        })).put(name, property);
    }
}
