package org.litkoit.directorscut.utils.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;
import org.litkoit.directorscut.utils.CameraConfig;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        CameraConfig config = CameraConfig.HANDLER.instance();

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
                                                newVal -> {
                                                    config.streamModeAutoSwitch = newVal;
                                                    CameraConfig.HANDLER.save();
                                                })
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .save(() -> CameraConfig.HANDLER.save())
                .build()
                .generateScreen(parentScreen);
    }
}
