package org.litkoit.directorscut.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.litkoit.directorscut.utils.CameraConfig;
import org.litkoit.directorscut.utils.types.DetachedCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    private void cancelBobView(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.detachedCameraActiveIndex != -1) {
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float modifyFovReturnValue(float original) {
        CameraConfig config = CameraConfig.HANDLER.instance();

        if (config.detachedCameraActiveIndex != -1) {
            if (config.detachedCameras.get(config.detachedCameraActiveIndex) instanceof DetachedCamera detachedCamera) {
                return detachedCamera.fov();
            }
        }

        return original;
    }
}
