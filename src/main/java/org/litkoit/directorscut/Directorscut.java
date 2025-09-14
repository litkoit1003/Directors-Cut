package org.litkoit.directorscut;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.litkoit.directorscut.utils.CameraConfig;

@Environment(EnvType.CLIENT)
public class Directorscut implements ClientModInitializer {

    public static final String MOD_ID = "directorscut";

    @Override
    public void onInitializeClient() {
        CameraConfig.HANDLER.load();

        KeyBindHandler.registerKeyBindings();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                KeyBindHandler.handleKeyPress();
            }
        });
    }
}
