package org.litkoit.directorscut.utils;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;


import java.util.ArrayList;
import java.util.List;

public class CameraConfig {
    public static ConfigClassHandler<CameraConfig> HANDLER = ConfigClassHandler.createBuilder(CameraConfig.class)
            .id(ResourceLocation.fromNamespaceAndPath("directorscut", "directorscut-config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("directorscut-config.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public int detachedCameraActiveIndex = 0;
    public boolean streamMode = false;

    @SerialEntry
    public List<DetachedCamera> detachedCameras = new ArrayList<>();

    @SerialEntry
    public boolean streamModeAutoSwitch = false;
}
