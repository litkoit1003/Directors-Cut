package org.litkoit.directorscut.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.litkoit.directorscut.utils.CameraConfig;
import org.litkoit.directorscut.utils.types.DetachedCamera;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CameraTooltipRenderer {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static final int TOOLTIP_BACKGROUND_COLOR = 0xF0100010;
    private static final int TOOLTIP_BORDER_COLOR = 0x505000FF;
    private static final int TOOLTIP_TEXT_COLOR = 0xFFFFFF;

    private static DetachedCamera currentHoveredCamera = null;
    public static void renderTooltips(GuiGraphics guiGraphics, List<DetachedCamera> cameras) {
        if (cameras == null || cameras.isEmpty() || CLIENT.player == null) {
            return;
        }

        DetachedCamera hoveredCamera = getHoveredCamera(cameras);

        if (hoveredCamera != currentHoveredCamera) {
            currentHoveredCamera = hoveredCamera;
        }

        if (currentHoveredCamera != null) {
            renderCameraTooltip(guiGraphics, currentHoveredCamera);
        }
    }

    private static DetachedCamera getHoveredCamera(List<DetachedCamera> cameras) {
        if (CLIENT.player == null) {
            return null;
        }

        Vec3 playerEyePos = CLIENT.player.getEyePosition();
        Vec3 lookDirection = CLIENT.player.getViewVector(1.0f);
        double maxDistance = 100.0;
        Vec3 rayEnd = playerEyePos.add(lookDirection.scale(maxDistance));

        for (DetachedCamera camera : cameras) {
            if (isMouseHoveringCamera(camera, playerEyePos, rayEnd)) {
                return camera;
            }
        }

        return null;
    }

    private static boolean isMouseHoveringCamera(DetachedCamera camera, Vec3 rayStart, Vec3 rayEnd) {
        float cameraScale = CameraConfig.HANDLER.instance().cameraScale;
        float size = cameraScale * 0.5f;

        Vec3 cameraWorldPos = new Vec3(camera.x(), camera.y(), camera.z());
        AABB cameraBounds = new AABB(
                cameraWorldPos.x - size, cameraWorldPos.y - size, cameraWorldPos.z - size,
                cameraWorldPos.x + size, cameraWorldPos.y + size, cameraWorldPos.z + size
        );

        return cameraBounds.clip(rayStart, rayEnd).isPresent();
    }

    private static void renderCameraTooltip(GuiGraphics guiGraphics, DetachedCamera camera) {
        List<Component> tooltipLines = createTooltipLines(camera);

        if (tooltipLines.isEmpty()) {
            return;
        }

        renderCustomTooltip(guiGraphics, tooltipLines);
    }

    private static List<Component> createTooltipLines(DetachedCamera camera) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.translatable("gui.directorscut.tooltip.camera").withStyle(style -> style.withColor(0xFFFF55)));

        lines.add(Component.translatable("gui.directorscut.tooltip.position",
                        String.format("%.1f", camera.x()),
                        String.format("%.1f", camera.y()),
                        String.format("%.1f", camera.z()))
                .withStyle(style -> style.withColor(0xAAAAAA)));

        lines.add(Component.translatable("gui.directorscut.tooltip.rotation",
                        String.format("%.1f", camera.yRot()),
                        String.format("%.1f", camera.xRot()))
                .withStyle(style -> style.withColor(0xAAAAAA)));

        lines.add(Component.translatable("gui.directorscut.tooltip.fov",
                        String.format("%.1f", camera.fov()))
                .withStyle(style -> style.withColor(0xAAAAAA)));

        lines.add(Component.literal(""));

        lines.add(Component.translatable("gui.directorscut.tooltip.edit")
                .withStyle(style -> style.withColor(0x55FF55)));

        lines.add(Component.translatable("gui.directorscut.tooltip.activate")
                .withStyle(style -> style.withColor(0x55FF55)));

        return lines;
    }

    private static void renderCustomTooltip(GuiGraphics guiGraphics, List<Component> lines) {
        if (lines.isEmpty()) {
            return;
        }

        int maxWidth = 0;
        int totalHeight;
        int lineHeight = CLIENT.font.lineHeight;
        int padding = 8;

        for (Component line : lines) {
            int lineWidth = CLIENT.font.width(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        totalHeight = lines.size() * lineHeight + padding;
        int tooltipWidth = maxWidth + padding * 2;

        int screenWidth = CLIENT.getWindow().getGuiScaledWidth();
        int screenHeight = CLIENT.getWindow().getGuiScaledHeight();

        int tooltipX = screenWidth / 2 + 10;
        int tooltipY = screenHeight / 2 + 10;

        RenderSystem.disableDepthTest();

        guiGraphics.fill(tooltipX - 3, tooltipY - 4,
                tooltipX + tooltipWidth + 3, tooltipY + totalHeight + 3,
                TOOLTIP_BACKGROUND_COLOR);

        guiGraphics.fill(tooltipX - 4, tooltipY - 5,
                tooltipX + tooltipWidth + 4, tooltipY - 4,
                TOOLTIP_BORDER_COLOR);
        guiGraphics.fill(tooltipX - 4, tooltipY + totalHeight + 3,
                tooltipX + tooltipWidth + 4, tooltipY + totalHeight + 4,
                TOOLTIP_BORDER_COLOR);
        guiGraphics.fill(tooltipX - 4, tooltipY - 4,
                tooltipX - 3, tooltipY + totalHeight + 3,
                TOOLTIP_BORDER_COLOR);
        guiGraphics.fill(tooltipX + tooltipWidth + 3, tooltipY - 4,
                tooltipX + tooltipWidth + 4, tooltipY + totalHeight + 3,
                TOOLTIP_BORDER_COLOR);

        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            int textY = tooltipY + i * lineHeight + padding / 2;
            guiGraphics.drawString(CLIENT.font, line, tooltipX + padding, textY, TOOLTIP_TEXT_COLOR);
        }

        RenderSystem.enableDepthTest();
    }

    public static DetachedCamera getCurrentHoveredCamera() {
        return currentHoveredCamera;
    }
}