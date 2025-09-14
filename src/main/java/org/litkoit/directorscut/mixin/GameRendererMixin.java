package org.litkoit.directorscut.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.litkoit.directorscut.utils.CameraConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    private void cancelBobView(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        if (CameraConfig.detachedCameraActiveIndex != 0) {
            ci.cancel();
        }
    }
}
