package org.litkoit.directorscut;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.litkoit.directorscut.gui.CameraListScreen;
import org.litkoit.directorscut.utils.CameraConfig;
import org.litkoit.directorscut.utils.DetachedCameraControl;
import org.litkoit.directorscut.utils.types.DetachedCamera;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyBindHandler {
    public static KeyMapping OPEN_CAMERA_MENU;
    public static KeyMapping MOVE_TO_VIEW;
    public static KeyMapping STREAMER_MODE;

    private static final Set<Integer> pressedKeys = new HashSet<>();

    public static void registerKeyBindings() {
        OPEN_CAMERA_MENU = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.directorscut.camera_menu",
                        GLFW.GLFW_KEY_F10,
                        "key.categories.directorscut"
                )
        );

        MOVE_TO_VIEW = KeyBindingHelper.registerKeyBinding(
                new KeyMapping("key.directorscut.move_to_view",
                        GLFW.GLFW_KEY_F9,
                        "key.categories.directorscut")
        );

        STREAMER_MODE = KeyBindingHelper.registerKeyBinding(
                new KeyMapping("key.directorscut.streamer_mode",
                        GLFW.GLFW_KEY_F8,
                        "key.categories.directorscut")
        );
    }

    public static void handleKeyPress() {
        Minecraft mc = Minecraft.getInstance();
        if (OPEN_CAMERA_MENU.consumeClick()) {
            mc.setScreen(new CameraListScreen(0));
            return;
        }

        CameraConfig config = CameraConfig.HANDLER.instance();
        List<DetachedCamera> cameras = new ArrayList<>(config.detachedCameras);

        for (int i = 0; i < cameras.size(); i++) {
            DetachedCamera camera = cameras.get(i);
            int keyCode;
            try {
                keyCode = camera.keybind();
            } catch (NullPointerException e) {
                return;
            }

            if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
                boolean isPressed = InputConstants.isKeyDown(mc.getWindow().getWindow(), keyCode);
                boolean wasPressed = pressedKeys.contains(keyCode);

                if (isPressed && !wasPressed) {
                    pressedKeys.add(keyCode);

                    if (i == config.detachedCameraActiveIndex) {
                        DetachedCameraControl.toggleFixedCamera(mc, i);
                    } else {
                        DetachedCameraControl.activateFixedCamera(mc, i);
                        config.detachedCameraActiveIndex = i;
                    }
                    break;
                } else if (!isPressed && wasPressed) {
                    pressedKeys.remove(keyCode);
                }
            }
        }

        if (config.waitPressForMove && MOVE_TO_VIEW.consumeClick()) {
            DetachedCameraControl.moveToView(mc, config.moveToIndex);
        }

        if (STREAMER_MODE.consumeClick()) {
            config.streamMode = !config.streamMode;
        }
    }
}

