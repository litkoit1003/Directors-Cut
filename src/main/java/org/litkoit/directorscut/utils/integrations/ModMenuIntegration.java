package org.litkoit.directorscut.utils.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;
import org.litkoit.directorscut.utils.CameraConfig;

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
                                .option(Option.<Double>createBuilder()
                                        .name(Component.translatable("gui.directorscut.config.fov_cone_size"))
                                        .description(OptionDescription.of(Component.translatable("gui.directorscut.config.fov_cone_size_description")))
                                        .binding(1.0d,
                                                () -> config.fovConeSize,
                                                fovConeSize -> {
                                                    config.fovConeSize = fovConeSize;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(doubleOption -> DoubleSliderControllerBuilder.create(doubleOption)
                                                .range(0.5d, 5.0d).step(0.1d).formatValue(fovConeSize -> Component.literal(decimalFormat.format(fovConeSize))))
                                        .build())
                                .build())
                        .build())
                .save(() -> CameraConfig.HANDLER.save())
                .build()
                .generateScreen(parentScreen);
    }
}
