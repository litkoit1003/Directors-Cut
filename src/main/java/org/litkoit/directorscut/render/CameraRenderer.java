package org.litkoit.directorscut.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.litkoit.directorscut.utils.CameraConfig;
import org.litkoit.directorscut.utils.types.DetachedCamera;

import java.util.ArrayList;
import java.util.List;

public class CameraRenderer {

    public static void renderCameras(PoseStack matrices, MultiBufferSource bufferSource, Camera camera) {
        Vec3 camPos = camera.getPosition();
        CameraConfig config = CameraConfig.HANDLER.instance();

        List<DetachedCamera> cameras = new ArrayList<>(config.detachedCameras);

        for (DetachedCamera dc : cameras) {
            if (dc == null) continue;

            matrices.pushPose();
            matrices.translate(dc.x() - camPos.x, dc.y() - camPos.y, dc.z() - camPos.z);

            Vec3 dir = directionFromRotation(dc.xRot(), dc.yRot());
            Vec3 end = dir.scale(2.0);

            renderLine(matrices, bufferSource, new Vec3(0, 0, 0), end, 1f, 0f, 1f);
            renderFOV(matrices, bufferSource, dir, dc.fov());

            matrices.popPose();
        }
    }

    private static void renderLine(PoseStack matrices, MultiBufferSource bufferSource, Vec3 start, Vec3 end, float r, float g, float a) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        vertexConsumer.vertex(matrices.last().pose(), (float) start.x, (float) start.y, (float) start.z)
                .color(r, g, (float) 0.0, a).normal(0,1,0).endVertex();
        vertexConsumer.vertex(matrices.last().pose(), (float) end.x, (float) end.y, (float) end.z)
                .color(r, g, (float) 0.0, a).normal(0,1,0).endVertex();
    }

    private static void renderFOV(PoseStack matrices, MultiBufferSource bufferSource, Vec3 dir, float fovDeg) {
        int circleSteps = CameraConfig.HANDLER.instance().fovRenderSteps;
        float fovRad = (float) Math.toRadians(fovDeg / 2);

        Vec3 up = Math.abs(dir.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = dir.cross(up).normalize();
        Vec3 upVec = right.cross(dir).normalize();

        Vec3 prev = null;
        for (int i = 0; i <= circleSteps; i++) {
            double angle = 2 * Math.PI * i / circleSteps;

            double dx = Math.cos(angle) * Math.sin(fovRad);
            double dy = Math.sin(angle) * Math.sin(fovRad);
            double dz = Math.cos(fovRad);

            Vec3 offset = right.scale(dx).add(upVec.scale(dy)).add(dir.normalize().scale(dz));
            Vec3 point = offset.normalize().scale(CameraConfig.HANDLER.instance().fovConeSize);

            renderLine(matrices, bufferSource, Vec3.ZERO, point, 0f, 1f, 0.6f);

            if (prev != null) {
                renderLine(matrices, bufferSource, prev, point, 0f, 1f, 0.6f);
            }
            prev = point;
        }
    }

    public static Vec3 directionFromRotation(float pitch, float yaw) {
        float f = -Mth.sin((float) Math.toRadians(yaw)) * Mth.cos((float) Math.toRadians(pitch));
        float g = -Mth.sin((float) Math.toRadians(pitch));
        float h = Mth.cos((float) Math.toRadians(yaw)) * Mth.cos((float) Math.toRadians(pitch));
        return new Vec3(f, g, h);
    }
}
