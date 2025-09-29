package org.litkoit.directorscut.utils;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.litkoit.directorscut.gui.CameraListScreen;
import org.litkoit.directorscut.utils.types.DetachedCamera;
import org.litkoit.directorscut.utils.types.KeyFrame;
import org.litkoit.directorscut.utils.types.KeyFrameType;

import java.util.List;

public class DetachedCameraControl {
    public static void activateFixedCamera(Minecraft mc, int index) {
        if (mc.player == null) return;
        CameraConfig config = CameraConfig.HANDLER.instance();

        config.detachedCameraActiveIndex = index;

        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);

        if (config.streamModeAutoSwitch) {
            config.streamMode = true;
        }
    }

    public static void deactivateFixedCamera(Minecraft mc) {
        CameraConfig config = CameraConfig.HANDLER.instance();
        config.detachedCameraActiveIndex = -1;

        mc.options.setCameraType(CameraType.FIRST_PERSON);

        if (config.streamModeAutoSwitch) {
            config.streamMode = false;
        }
    }

    public static void toggleFixedCamera(Minecraft mc, int index) {
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.detachedCameraActiveIndex != -1) {
            deactivateFixedCamera(mc);
        } else {
            activateFixedCamera(mc, index);
        }
    }

    public static void moveToView(Minecraft mc, int index) {
        if (mc.player == null) return;
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.moveToIndex == -1) return;

        if (config.detachedCameras.get(index) instanceof DetachedCamera oldCamera) {
            List<KeyFrame> keyFrames = oldCamera.keyframes();
            keyFrames.set(0, new KeyFrame(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ(), mc.player.getXRot(),
                    Mth.wrapDegrees(mc.player.getYRot()), oldCamera.fov(), 0.0, KeyFrameType.START));

            config.detachedCameras.set(index, new DetachedCamera(mc.player.getX(),
                    mc.player.getEyeY(),
                    mc.player.getZ(),
                    mc.player.getXRot(),
                    Mth.wrapDegrees(mc.player.getYRot()), oldCamera.fov(), oldCamera.keybind(), keyFrames));
            mc.setScreen(new CameraListScreen(config.moveToIndex));
            config.moveToIndex = -1;
        }
    }
}
