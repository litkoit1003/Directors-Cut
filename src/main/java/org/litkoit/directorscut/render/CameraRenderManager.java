package org.litkoit.directorscut.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.litkoit.directorscut.utils.CameraConfig;
import org.litkoit.directorscut.utils.types.DetachedCamera;

import java.util.List;

public class CameraRenderManager {
    private static CameraRenderManager INSTANCE;

    private final CameraRenderer renderer;
    private List<DetachedCamera> activeCameras;
    private boolean showDebugInfo = true;

    private CameraRenderManager() {
        this.renderer = new CameraRenderer(showDebugInfo);
        this.activeCameras = CameraConfig.HANDLER.instance().detachedCameras;

        WorldRenderEvents.AFTER_ENTITIES.register(this::onRenderWorld);
    }

    public static CameraRenderManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CameraRenderManager();
        }
        return INSTANCE;
    }

    private void onRenderWorld(WorldRenderContext context) {
        if (CameraConfig.HANDLER.instance().streamMode) {
            return;
        }
        if (activeCameras == null || activeCameras.isEmpty()) {
            return;
        }

        PoseStack poseStack = context.matrixStack();
        if (poseStack == null) return;

        renderer.renderAllCameras(poseStack, activeCameras);
    }

    public void setDebugInfo(boolean showDebugInfo) {
        this.showDebugInfo = showDebugInfo;
    }

    public void update() {
        this.activeCameras = CameraConfig.HANDLER.instance().detachedCameras;
    }
}