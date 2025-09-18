package org.litkoit.directorscut.utils;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.litkoit.directorscut.utils.types.DetachedCamera;

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
}
