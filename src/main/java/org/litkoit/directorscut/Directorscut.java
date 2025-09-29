package org.litkoit.directorscut;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.litkoit.directorscut.render.CameraRenderManager;
import org.litkoit.directorscut.render.CameraTooltipRenderer;
import org.litkoit.directorscut.utils.CameraConfig;


@Environment(EnvType.CLIENT)
public class Directorscut implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CameraConfig.HANDLER.load();

        KeyBindHandler.registerKeyBindings();

        CameraRenderManager manager = CameraRenderManager.getInstance();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                KeyBindHandler.handleKeyPress();
            }
            manager.update();
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (Minecraft.getInstance().screen == null && CameraConfig.HANDLER.instance().detachedCameras != null
                    && !CameraConfig.HANDLER.instance().detachedCameras.isEmpty()) {

                GuiGraphics guiGraphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());

                CameraTooltipRenderer.renderTooltips(guiGraphics,
                        CameraConfig.HANDLER.instance().detachedCameras);

                guiGraphics.flush();
            }
        });
    }
}
