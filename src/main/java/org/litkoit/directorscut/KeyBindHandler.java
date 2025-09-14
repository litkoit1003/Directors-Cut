package org.litkoit.directorscut;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.litkoit.directorscut.utils.DetachedCameraControl;
import org.lwjgl.glfw.GLFW;

public class KeyBindHandler {
    public static KeyMapping TOGGLE_FIXED_CAMERA;

    public static void registerKeyBindings() {
        TOGGLE_FIXED_CAMERA = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.directorscut.toggle",
                        GLFW.GLFW_KEY_F9,
                        "key.categories.directorscut"
                )
        );
    }

    public static void handleKeyPress() {
        if (TOGGLE_FIXED_CAMERA.consumeClick()) {
            DetachedCameraControl.toggleFixedCamera(Minecraft.getInstance());
        }
    }
}

