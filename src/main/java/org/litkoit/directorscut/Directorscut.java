package org.litkoit.directorscut;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.litkoit.directorscut.render.CameraRenderer;
import org.litkoit.directorscut.utils.CameraConfig;

@Environment(EnvType.CLIENT)
public class Directorscut implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CameraConfig.HANDLER.load();

        KeyBindHandler.registerKeyBindings();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                KeyBindHandler.handleKeyPress();
            }
        });

        WorldRenderEvents.LAST.register(context -> {
            if (!CameraConfig.HANDLER.instance().streamMode) {
                CameraRenderer.renderCameras(context.matrixStack(), context.consumers(), context.camera());
            }
        });
    }
}
