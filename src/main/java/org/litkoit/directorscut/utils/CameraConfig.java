package org.litkoit.directorscut.utils;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.litkoit.directorscut.utils.types.DetachedCamera;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CameraConfig {
    public static ConfigClassHandler<CameraConfig> HANDLER = ConfigClassHandler.createBuilder(CameraConfig.class)
            .id(new ResourceLocation("directorscut", "directorscut-config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("directorscut-config.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public int detachedCameraActiveIndex = -1;
    public int moveToIndex = -1;
    public boolean streamMode = false;
    public boolean waitPressForMove = false;


    public final List<DetachedCamera> detachedCameras = new ArrayList<>();

    @SerialEntry
    public boolean streamModeAutoSwitch = true;

    @SerialEntry
    public int fovRenderSteps = 32;

    @SerialEntry
    public double fovConeSize = 1.0;

    @SerialEntry
    public Color fovConeColor = new Color(0x8800FF00, true);

    @SerialEntry
    public float cameraScale = 0.5f;

    @SerialEntry
    public Color cameraBaseColor = new Color(0xFFFFFFFF, true);

    @SerialEntry
    public Color cameraHoverColor = new Color(0xFFFFFF00, true);

    @SerialEntry
    public Color cameraLineColor = new Color(0xFFFF0000, true);

    @SerialEntry
    public float cameraLineSize = 1.0f;
}
