package com.ldtteam.blockui.mixin;

import com.ldtteam.blockui.fabric.ModelManagerExtension;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
public class ModelManagerMixin implements ModelManagerExtension {
    @Unique private ModelBakery modelBakery;

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
    private void blockui$setModelBakery(ModelManager.ReloadState reloadState, ProfilerFiller profiler, CallbackInfo ci, @Local ModelBakery modelBakery) {
        this.modelBakery = modelBakery;
    }

    @Override
    public ModelBakery blockui$getModelBakery() {
        return this.modelBakery;
    }
}
