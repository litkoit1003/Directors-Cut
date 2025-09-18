package org.litkoit.directorscut.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.litkoit.directorscut.utils.CameraConfig;
import org.litkoit.directorscut.utils.types.DetachedCamera;
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

    @Inject(method = "setup", at = @At("TAIL"))
    private void applyFixedCameraTransform(BlockGetter level, Entity entity, boolean detached, boolean mirrored, float partialTick, CallbackInfo ci) {
        CameraConfig config = CameraConfig.HANDLER.instance();

        if (config.detachedCameraActiveIndex != -1) {
            DetachedCamera camera = config.detachedCameras.get(config.detachedCameraActiveIndex);
            this.xRot = camera.xRot();
            this.yRot = camera.yRot();

            setPosition(camera.x(), camera.y(), camera.z());
            setRotation(camera.yRot(), camera.xRot());
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void preventCameraMovement(double d, double e, double f, CallbackInfo ci) {
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.detachedCameraActiveIndex != -1) {
            ci.cancel();
        }
    }

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
}