package org.litkoit.directorscut.mixin;

import net.minecraft.client.Camera;
import org.litkoit.directorscut.utils.CameraConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER))
    private void applyFixedCameraTransform(CallbackInfo ci) {
        CameraConfig config = CameraConfig.HANDLER.instance();

        if (config.detachedCameraActiveIndex != 0) {
            setPosition(config.detachedCameras.get(config.detachedCameraActiveIndex).x(),
                    config.detachedCameras.get(config.detachedCameraActiveIndex).y(),
                    config.detachedCameras.get(config.detachedCameraActiveIndex).z());

            setRotation(config.detachedCameras.get(config.detachedCameraActiveIndex).yRot(),
                    config.detachedCameras.get(config.detachedCameraActiveIndex).xRot());
        }
    }

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
}
