package org.litkoit.directorscut.mixin;

import net.minecraft.client.CameraType;
import org.litkoit.directorscut.utils.CameraConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CameraType.class)
public class CameraTypeMixin {
    @Inject(method = "isFirstPerson", at = @At("HEAD"), cancellable = true)
    private void overrideIsFirstPerson(CallbackInfoReturnable<Boolean> cir) {
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.detachedCameraActiveIndex != -1) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isMirrored", at = @At("HEAD"), cancellable = true)
    private void overrideIsMirrored(CallbackInfoReturnable<Boolean> cir) {
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.detachedCameraActiveIndex != -1) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "cycle", at = @At("HEAD"))
    private void onCameraCycle(CallbackInfoReturnable<CameraType> cir) {
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.detachedCameraActiveIndex != -1) {
            config.detachedCameraActiveIndex = -1;
        }
    }
}
