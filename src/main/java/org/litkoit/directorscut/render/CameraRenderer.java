package org.litkoit.directorscut.render;

import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.litkoit.directorscut.utils.CameraConfig;
import org.litkoit.directorscut.utils.types.DetachedCamera;

import java.util.List;

@Environment(EnvType.CLIENT)
public class CameraRenderer {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private final boolean renderDebugInfo;

    public CameraRenderer(boolean renderDebugInfo) {
        this.renderDebugInfo = renderDebugInfo;
    }

    public void render(PoseStack matrices, DetachedCamera camera) {
        if (camera == null || CLIENT.player == null) return;

        Vec3 cameraPos = CLIENT.gameRenderer.getMainCamera().getPosition();

        matrices.pushPose();

        double relX = camera.x() - cameraPos.x;
        double relY = camera.y() - cameraPos.y;
        double relZ = camera.z() - cameraPos.z;

        matrices.translate(relX, relY, relZ);

        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-camera.yRot()));
        matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camera.xRot()));

        boolean isHovered = isMouseHoveringCamera(camera);
        renderCameraBody(matrices, isHovered);

        if (renderDebugInfo) {
            renderViewDirection(matrices);
        }

        renderFOVCone(matrices, camera.fov());

        matrices.popPose();
    }

    private boolean isMouseHoveringCamera(DetachedCamera camera) {
        if (CLIENT.player == null || CameraConfig.HANDLER.instance().streamMode) {
            return false;
        }

        Vec3 playerEyePos = CLIENT.player.getEyePosition();
        Vec3 lookDirection = CLIENT.player.getViewVector(1.0f);

        float cameraScale = CameraConfig.HANDLER.instance().cameraScale;
        float size = cameraScale * 0.5f;

        Vec3 cameraWorldPos = new Vec3(camera.x(), camera.y(), camera.z());
        AABB cameraBounds = new AABB(
                cameraWorldPos.x - size, cameraWorldPos.y - size, cameraWorldPos.z - size,
                cameraWorldPos.x + size, cameraWorldPos.y + size, cameraWorldPos.z + size
        );

        double maxDistance = 100.0;
        Vec3 rayEnd = playerEyePos.add(lookDirection.scale(maxDistance));

        return cameraBounds.clip(playerEyePos, rayEnd).isPresent();
    }

    private void renderCameraBody(PoseStack matrices, boolean isHovered) {
        Matrix4f matrix = matrices.last().pose();

        MultiBufferSource.BufferSource bufferSource = CLIENT.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        float camera_scale = CameraConfig.HANDLER.instance().cameraScale;
        float size = camera_scale * 0.5f;

        CameraConfig config = CameraConfig.HANDLER.instance();

        int color = isHovered ? config.cameraHoverColor.getRGB() : config.cameraBaseColor.getRGB();

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        renderWireframeCube(vertexConsumer, matrix, size, r, g, b, a);

        bufferSource.endBatch(RenderType.lines());
    }

    private void renderWireframeCube(VertexConsumer vertexConsumer, Matrix4f matrix, float size, int r, int g, int b, int a) {
        addColoredVertex(vertexConsumer, matrix, -size, -size, size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, size, -size, size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, size, -size, size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, size, size, size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, size, size, size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, -size, size, size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, -size, size, size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, -size, -size, size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, -size, -size, -size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, size, -size, -size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, size, -size, -size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, size, size, -size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, size, size, -size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, -size, size, -size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, -size, size, -size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, -size, -size, -size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, -size, -size, size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, -size, -size, -size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, size, -size, size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, size, -size, -size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, size, size, size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, size, size, -size, r, g, b, a);

        addColoredVertex(vertexConsumer, matrix, -size, size, size, r, g, b, a);
        addColoredVertex(vertexConsumer, matrix, -size, size, -size, r, g, b, a);
    }

    private void renderViewDirection(PoseStack matrices) {
        Matrix4f matrix = matrices.last().pose();

        CameraConfig config = CameraConfig.HANDLER.instance();

        MultiBufferSource.BufferSource bufferSource = CLIENT.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        addColoredVertex(vertexConsumer, matrix, 0f, 0f, 0f, config.cameraLineColor.getRGB());
        addColoredVertex(vertexConsumer, matrix, 0f, 0f, config.cameraLineSize, config.cameraLineColor.getRGB());

        bufferSource.endBatch(RenderType.lines());
    }

    private void renderFOVCone(PoseStack matrices, float fov) {
        Matrix4f matrix = matrices.last().pose();

        MultiBufferSource.BufferSource bufferSource = CLIENT.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        float distance = (float) CameraConfig.HANDLER.instance().fovConeSize;
        float halfFov = fov * 0.5f;
        float tanFov = (float) Math.tan(Math.toRadians(halfFov));
        float coneRadius = distance * tanFov;

        int segments = CameraConfig.HANDLER.instance().fovRenderSteps;
        Vector3f origin = new Vector3f(0, 0, 0);

        for (int i = 0; i < segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float nextAngle = (float) (2 * Math.PI * (i + 1) / segments);

            float x1 = coneRadius * (float) Math.cos(angle);
            float y1 = coneRadius * (float) Math.sin(angle);
            float x2 = coneRadius * (float) Math.cos(nextAngle);
            float y2 = coneRadius * (float) Math.sin(nextAngle);

            CameraConfig config = CameraConfig.HANDLER.instance();

            if (i % 4 == 0) {
                addColoredVertex(vertexConsumer, matrix, origin.x(), origin.y(), origin.z(), config.fovConeColor.getRGB());
                addColoredVertex(vertexConsumer, matrix, x1, y1, distance, config.fovConeColor.getRGB());
            }

            addColoredVertex(vertexConsumer, matrix, x1, y1, distance, config.fovConeColor.getRGB());
            addColoredVertex(vertexConsumer, matrix, x2, y2, distance, config.fovConeColor.getRGB());
        }

        bufferSource.endBatch(RenderType.lines());
    }

    private void addColoredVertex(VertexConsumer vertexConsumer, Matrix4f matrix, float x, float y, float z, int color) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        vertexConsumer.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setNormal(0.0f, 1.0f, 0.0f);
    }

    private void addColoredVertex(VertexConsumer vertexConsumer, Matrix4f matrix, float x, float y, float z, int r, int g, int b, int a) {
        vertexConsumer.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setNormal(0.0f, 1.0f, 0.0f);
    }

    public void renderAllCameras(PoseStack matrices, List<DetachedCamera> cameras) {
        if (cameras == null || cameras.isEmpty()) return;

        for (DetachedCamera camera : cameras) {
            render(matrices, camera);
        }
    }
}