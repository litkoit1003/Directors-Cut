package org.litkoit.directorscut.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.litkoit.directorscut.render.CameraTooltipRenderer;
import org.litkoit.directorscut.utils.CameraConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class TooltipRenderMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderCameraTooltips(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.player != null && this.minecraft.screen == null && this.minecraft.level != null && !CameraConfig.HANDLER.instance().streamMode) {
            if (CameraConfig.HANDLER.instance().detachedCameras != null
                    && !CameraConfig.HANDLER.instance().detachedCameras.isEmpty()) {

                CameraTooltipRenderer.renderTooltips(guiGraphics,
                        CameraConfig.HANDLER.instance().detachedCameras);
            }
        }
    }
}
