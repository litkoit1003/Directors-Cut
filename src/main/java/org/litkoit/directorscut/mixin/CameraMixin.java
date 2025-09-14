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

    @Shadow
    private float xRot;
    @Shadow
    private float yRot;

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER))
    private void applyFixedCameraTransform(CallbackInfo ci) {
        if (CameraConfig.detachedCameraActiveIndex != 0) {
            setPosition(CameraConfig.detachedCameras.get(CameraConfig.detachedCameraActiveIndex).x(),
                    CameraConfig.detachedCameras.get(CameraConfig.detachedCameraActiveIndex).y(),
                    CameraConfig.detachedCameras.get(CameraConfig.detachedCameraActiveIndex).z());

            setRotation(CameraConfig.detachedCameras.get(CameraConfig.detachedCameraActiveIndex).yRot(),
                    CameraConfig.detachedCameras.get(CameraConfig.detachedCameraActiveIndex).xRot());
        }
    }

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
}
