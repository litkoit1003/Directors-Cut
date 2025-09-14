package org.litkoit.directorscut.utils;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class DetachedCameraControl {
    public static void activateFixedCamera(Minecraft mc) {
        if (mc.player == null) return;

        CameraConfig.detachedCameraActiveIndex = 1;

        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);

        if (mc.player != null) {
            mc.player.displayClientMessage(
                    Component.literal("Fixed camera activated"),
                    true
            );
        }
    }

    public static void deactivateFixedCamera(Minecraft mc) {
        CameraConfig.detachedCameraActiveIndex = 0;

        mc.options.setCameraType(CameraType.FIRST_PERSON);

        if (mc.player != null) {
            mc.player.displayClientMessage(
                    Component.literal("Fixed camera deactivated"),
                    true
            );
        }
    }

    public static void toggleFixedCamera(Minecraft mc) {
        if (CameraConfig.detachedCameraActiveIndex != 0) {
            deactivateFixedCamera(mc);
        } else {
            activateFixedCamera(mc);
        }
    }
}
