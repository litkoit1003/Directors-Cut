package org.litkoit.directorscut.utils.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.network.chat.Component;
import org.litkoit.directorscut.utils.CameraConfig;

import java.awt.*;
import java.text.DecimalFormat;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        CameraConfig config = CameraConfig.HANDLER.instance();
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("gui.directorscut.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("gui.directorscut.config.main_category"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("gui.directorscut.config.main_group"))
                                .description(OptionDescription.of(Component.translatable("gui.directorscut.config.main_group_description")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.stream_mode_option"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.stream_mode_option_description")))
                                        .binding(true,
                                                () -> config.streamModeAutoSwitch,
                                                streamModeAutoSwitch -> {
                                                    config.streamModeAutoSwitch = streamModeAutoSwitch;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("gui.directorscut.config.render_group"))
                                .description(OptionDescription.of(Component.translatable("gui.directorscut.config.render_group_description")))
                                .option(Option.<Double>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.fov_cone_size"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.fov_cone_size_description")))
                                        .binding(1.0d,
                                                () -> config.fovConeSize,
                                                fovConeSize -> {
                                                    config.fovConeSize = fovConeSize;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(fovConeSizeOption -> DoubleSliderControllerBuilder.create(fovConeSizeOption)
                                                .range(0.5d, 5.0d).step(0.1d).formatValue(fovConeSize -> Component.literal(decimalFormat.format(fovConeSize))))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.fov_render_steps"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.fov_render_steps_description")))
                                        .binding(32,
                                                () -> config.fovRenderSteps,
                                                fovRenderSteps -> {
                                                    config.fovRenderSteps = fovRenderSteps;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption)
                                                .range(4,64).step(1).formatValue(fovRenderSteps -> Component.literal(fovRenderSteps + " " + Component.translatable("gui.directorscut.config.fov_steps_name").getString())))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.fov_cone_color"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.fov_cone_color_description")))
                                        .binding(new Color(0x8800FF00, true),
                                                () -> config.fovConeColor,
                                                fovConeColor -> {
                                                    config.fovConeColor = fovConeColor;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(fovConeColorOption -> ColorControllerBuilder.create(fovConeColorOption)
                                                .allowAlpha(true))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.camera_line_size"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.camera_line_size_description")))
                                        .binding(1.0f,
                                                () -> config.cameraLineSize,
                                                cameraLineSize -> {
                                                    config.cameraLineSize = cameraLineSize;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(cameraLineSizeOption -> FloatSliderControllerBuilder.create(cameraLineSizeOption)
                                                .step(0.1f).range(0.5f, 5.0f).formatValue(cameraLineSizeA -> Component.literal(decimalFormat.format(cameraLineSizeA))))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.camera_line_color"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.camera_line_color_description")))
                                        .binding(new Color(0xFFFF0000, true),
                                                () -> config.cameraLineColor,
                                                cameraLineColor -> {
                                                    config.cameraLineColor = cameraLineColor;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(cameraLineColorOption -> ColorControllerBuilder.create(cameraLineColorOption)
                                                .allowAlpha(true))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.base_camera_color"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.base_camera_color_description")))
                                        .binding(new Color(0xFFFFFFFF, true),
                                                () -> config.cameraBaseColor,
                                                cameraBaseColor -> {
                                                    config.cameraBaseColor = cameraBaseColor;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(cameraBaseColorOption -> ColorControllerBuilder.create(cameraBaseColorOption)
                                                .allowAlpha(true))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.hover_camera_color"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.hover_camera_color_description")))
                                        .binding(new Color(0xFFFFFF00, true),
                                                () -> config.cameraHoverColor,
                                                cameraHoverColor -> {
                                                    config.cameraHoverColor = cameraHoverColor;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(cameraHoverColorOption -> ColorControllerBuilder.create(cameraHoverColorOption)
                                                .allowAlpha(true))
                                        .build())
                                .build())
                        .build())
                .save(() -> CameraConfig.HANDLER.save())
                .build()
                .generateScreen(parentScreen);
    }
}
