package org.litkoit.directorscut.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.option.*;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.litkoit.directorscut.utils.CameraConfig;
import org.litkoit.directorscut.utils.types.DetachedCamera;
import org.litkoit.directorscut.utils.types.KeyFrame;
import org.litkoit.directorscut.utils.types.KeyFrameType;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CameraListScreen extends SpruceScreen {
    private final int slot;
    private final int maxSlots = 50;

    private double tempX, tempY, tempZ;
    private float tempXRot, tempYRot, tempFov;
    private int tempKeybind;
    private List<KeyFrame> tempKeyFrames;
    private boolean waitingForKey = false;
    private SpruceButtonWidget keyButton;

    private boolean hasUnsavedChanges = false;

    private SpruceButtonWidget saveButton;
    private SpruceButtonWidget resetButton;
    private SpruceButtonWidget prevButton;
    private SpruceButtonWidget nextButton;

    public CameraListScreen(int slot) {
        super(Component.translatable("gui.directorscut.camera_list.title", slot + 1));
        this.slot = Math.max(0, Math.min(slot, maxSlots - 1));
    }

    @Override
    protected void init() {
        super.init();

        this.clearWidgets();

        DetachedCamera camera = getCurrentCamera();

        addNavigationButtons();

        if (camera != null) {
            initializeTempValues(camera);
            buildCameraEditInterface();
        } else {
            buildEmptySlotInterface();
        }
    }

    private DetachedCamera getCurrentCamera() {
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.detachedCameras.size() > slot) {
            return config.detachedCameras.get(slot);
        }
        return null;
    }

    private void initializeTempValues(DetachedCamera camera) {
        tempX = camera.x();
        tempY = camera.y();
        tempZ = camera.z();
        tempXRot = camera.xRot();
        tempYRot = camera.yRot();
        tempFov = camera.fov();
        tempKeybind = camera.keybind();
        tempKeyFrames = camera.keyframes();
    }

    private void buildCameraEditInterface() {
        SpruceContainerWidget mainContainer = new SpruceContainerWidget(Position.of(0, 35), width, height - 90);

        SpruceOptionListWidget optionList = new SpruceOptionListWidget(Position.of(width / 2 - 200, 45), 400, height - 140);

        optionList.addSingleOptionEntry(createCoordinateOption("X", () -> tempX, value -> {
            tempX = value;
            markUnsaved();
        }));

        optionList.addSingleOptionEntry(createCoordinateOption("Y", () -> tempY, value -> {
            tempY = value;
            markUnsaved();
        }));

        optionList.addSingleOptionEntry(createCoordinateOption("Z", () -> tempZ, value -> {
            tempZ = value;
            markUnsaved();
        }));

        optionList.addSingleOptionEntry(new SpruceSeparatorOption("gui.directorscut.camera_list.rotation_section", true, null));

        optionList.addSingleOptionEntry(createRotationSlider(Component.translatable("gui.directorscut.camera_list.xRot").getString(), -90.0f, 90.0f,
                () -> tempXRot, value -> {
                    tempXRot = value;
                    markUnsaved();
                }));

        optionList.addSingleOptionEntry(createRotationSlider(Component.translatable("gui.directorscut.camera_list.yRot").getString(), -180.0f, 180.0f,
                () -> tempYRot, value -> {
                    tempYRot = value;
                    markUnsaved();
                }));

        optionList.addSingleOptionEntry(createFovSlider());

        optionList.addSingleOptionEntry(createMoveCameraOption());

        optionList.addSingleOptionEntry(new SpruceSeparatorOption("gui.directorscut.camera_list.controls_section", true, null));

        optionList.addSingleOptionEntry(createKeybindOption());

        //optionList.addSingleOptionEntry(new SpruceSeparatorOption("gui.directorscut.camera_list.animations", true, null));
        // TODO Animation editor
        //optionList.addSingleOptionEntry(createKeyFrameEditorLink());

        mainContainer.addChild(optionList);
        this.addRenderableWidget(mainContainer);

        addCameraActionButtons();
    }

    private SpruceOption createCoordinateOption(String axis, DoubleSupplier getter, DoubleConsumer setter) {
        return new SpruceDoubleInputOption("gui.directorscut.camera_list.coordinate" + axis.toLowerCase(),
                getter::getAsDouble,
                setter::accept,
                Component.translatable("gui.directorscut.camera_list.coordinate_tooltip", axis));
    }

    private SpruceOption createRotationSlider(String name, float min, float max, FloatSupplier getter, FloatConsumer setter) {
        return new SpruceDoubleOption(name,
                min, max, 1.0f,
                () -> (double) getter.get(),
                value -> setter.accept(value.floatValue()),
                option -> Component.literal(name + ": " + String.format("%.1f°", getter.get())),
                Component.translatable("gui.directorscut.camera_list.rotation_tooltip"));
    }

    private SpruceOption createKeyFrameEditorLink() {
        return SpruceSimpleActionOption.of("gui.directorscut.camera_list.open_keyframe_editor",
                this::openKeyframeEditor,
                Component.translatable("gui.directorscut.camera_list.keyframe_editor_tooltip"));
    }

    private void openKeyframeEditor(SpruceButtonWidget btn) {

    }

    private SpruceOption createFovSlider() {
        return new SpruceDoubleOption("gui.directorscut.camera_list.fov",
                30.0, 110.0, 1.0f,
                () -> (double) tempFov,
                value -> {
                    tempFov = value.floatValue();
                    markUnsaved();
                },
                option -> Component.translatable("gui.directorscut.camera_list.fov_display", String.format("%.0f", tempFov)),
                Component.translatable("gui.directorscut.camera_list.fov_tooltip"));
    }

    private SpruceOption createKeybindOption() {
        if (tempKeybind == GLFW.GLFW_KEY_UNKNOWN) {
            return SpruceSimpleActionOption.of("gui.directorscut.camera_list.keybind",
                    this::openKeybindSelectionScreen,
                    Component.translatable("gui.directorscut.camera_list.keybind_tooltip"));
        }else {
            String keyName = GLFW.glfwGetKeyName(tempKeybind, GLFW.glfwGetKeyScancode(tempKeybind));
            if (keyName == null) return SpruceSimpleActionOption.of("gui.directorscut.camera_list.keybind",
                    this::openKeybindSelectionScreen,
                    Component.translatable("gui.directorscut.camera_list.keybind_tooltip"));

            return SpruceSimpleActionOption.of(keyName.toUpperCase(),
                    this::openKeybindSelectionScreen,
                    Component.translatable("gui.directorscut.camera_list.keybind_tooltip"));
        }
    }

    private SpruceOption createMoveCameraOption() {
        return SpruceSimpleActionOption.of("key.directorscut.move_to_view",
                btn -> {
            CameraConfig.HANDLER.instance().waitPressForMove = true;
            CameraConfig.HANDLER.instance().moveToIndex = slot;
            this.onClose();
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("msg.directorscut.press_key_to_move"), true);
            }
        }, null);
    }

    private void buildEmptySlotInterface() {
        int buttonWidth = 200;
        int buttonHeight = 20;

        SpruceButtonWidget createButton = new SpruceButtonWidget(
                Position.of(width / 2 - buttonWidth / 2, height / 2 - 40),
                buttonWidth, buttonHeight,
                Component.translatable("gui.directorscut.camera_list.create_camera"),
                btn -> createNewCamera()
        );

        this.addRenderableWidget(createButton);
    }

    private void addNavigationButtons() {
        prevButton = new SpruceButtonWidget(
                Position.of(20, height / 2 - 30), 30, 20,
                Component.literal("◀"),
                btn -> navigateToSlot(slot - 1)
        );
        prevButton.setActive(slot > 0);

        nextButton = new SpruceButtonWidget(
                Position.of(width - 50, height / 2 - 30), 30, 20,
                Component.literal("▶"),
                btn -> navigateToSlot(slot + 1)
        );
        nextButton.setActive(slot < maxSlots - 1);

        Component slotIndicator = Component.translatable("gui.directorscut.camera_list.slot_indicator", slot + 1, maxSlots);
        int slotWidth = font.width(slotIndicator);
        SpruceLabelWidget slotLabel = new SpruceLabelWidget(
                Position.of(width / 2 - slotWidth, 10),
                slotIndicator,
                width, true
        );

        this.addRenderableWidget(prevButton);
        this.addRenderableWidget(nextButton);
        this.addRenderableWidget(slotLabel);
    }

    private void addCameraActionButtons() {
        int buttonY = height - 40;

        saveButton = new SpruceButtonWidget(
                Position.of(width / 2 - 205, buttonY), 100, 20,
                Component.translatable("gui.directorscut.camera_list.save"),
                btn -> saveChanges()
        );
        updateButtonStates();

        resetButton = new SpruceButtonWidget(
                Position.of(width / 2 - 100, buttonY), 100, 20,
                Component.translatable("gui.directorscut.camera_list.reset"),
                btn -> resetChanges()
        );
        updateButtonStates();

        SpruceButtonWidget deleteButton = new SpruceButtonWidget(
                Position.of(width / 2 + 5, buttonY), 100, 20,
                Component.translatable("gui.directorscut.camera_list.delete"),
                btn -> deleteCamera()
        );

        SpruceButtonWidget closeButton = new SpruceButtonWidget(
                Position.of(width / 2 + 110, buttonY), 100, 20,
                SpruceTexts.GUI_DONE,
                btn -> onClose()
        );

        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(deleteButton);
        this.addRenderableWidget(closeButton);
    }

    private void updateButtonStates() {
        if (saveButton != null) {
            saveButton.setActive(hasUnsavedChanges);
        }
        if (resetButton != null) {
            resetButton.setActive(hasUnsavedChanges);
        }
        if (prevButton != null) {
            prevButton.setActive(slot > 0);
        }
        if (nextButton != null) {
            nextButton.setActive(slot < maxSlots - 1);
        }
    }

    private void createNewCamera() {
        CameraConfig config = CameraConfig.HANDLER.instance();

        while (config.detachedCameras.size() <= slot) {
            config.detachedCameras.add(null);
        }

        List<KeyFrame> keyFrames = new ArrayList<>();

        keyFrames.add(new KeyFrame(0, 70, 0, 0, 0, 70.0f, 0.0, KeyFrameType.START));

        DetachedCamera newCamera = new DetachedCamera(0, 70, 0, 0, 0, 70.0f, GLFW.GLFW_KEY_UNKNOWN, keyFrames);
        config.detachedCameras.set(slot, newCamera);
        CameraConfig.HANDLER.save();

        Minecraft.getInstance().setScreen(new CameraListScreen(slot));
    }

    private void saveChanges() {
        CameraConfig config = CameraConfig.HANDLER.instance();
        DetachedCamera updatedCamera = new DetachedCamera(tempX, tempY, tempZ, tempXRot, tempYRot, tempFov, tempKeybind, tempKeyFrames);

        config.detachedCameras.set(slot, updatedCamera);
        CameraConfig.HANDLER.save();

        hasUnsavedChanges = false;
        updateButtonStates();
    }

    private void resetChanges() {
        DetachedCamera camera = getCurrentCamera();
        if (camera != null && minecraft != null) {
            initializeTempValues(camera);
            hasUnsavedChanges = false;
            this.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        }
    }

    private void deleteCamera() {
        CameraConfig config = CameraConfig.HANDLER.instance();
        if (config.detachedCameras.size() > slot) {
            config.detachedCameras.set(slot, null);
            config.detachedCameraActiveIndex = -1;
            CameraConfig.HANDLER.save();
        }

        Minecraft.getInstance().setScreen(new CameraListScreen(slot));
    }

    private void navigateToSlot(int newSlot) {
        if (newSlot < 0 || newSlot >= maxSlots) {
            return;
        }

        if (hasUnsavedChanges) {
            saveChanges();
        }
        Minecraft.getInstance().setScreen(new CameraListScreen(newSlot));
    }

    private void openKeybindSelectionScreen(SpruceButtonWidget btn) {
        btn.setMessage(Component.translatable("gui.directorscut.camera_list.press_key"));
        keyButton = btn;
        waitingForKey = true;
    }

    private void markUnsaved() {
        hasUnsavedChanges = true;
        updateButtonStates();
    }

    @Override
    public void renderTitle(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        DetachedCamera camera = getCurrentCamera();

        Component title = Component.translatable("gui.directorscut.camera_list.title", slot + 1);
        int titleWidth = font.width(title);
        guiGraphics.drawString(font, title, width / 2 - titleWidth / 2 + (camera != null ? 10 : 0), 8, 0xFFFFFF);

        if (hasUnsavedChanges) {
            guiGraphics.drawString(font, Component.literal("*").withStyle(style -> style.withColor(0xFF6B6B)),
                    width / 2 + titleWidth / 2 + 15, 8, 0xFF6B6B);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey) {
            tempKeybind = keyCode;
            waitingForKey = false;

            String keyName = InputConstants.getKey(keyCode, scanCode).getDisplayName().getString();
            keyButton.setMessage(Component.literal(keyName));

            markUnsaved();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (hasUnsavedChanges) {
            saveChanges();
        }
        super.onClose();
    }

    @FunctionalInterface
    private interface DoubleSupplier {
        double getAsDouble();
    }

    @FunctionalInterface
    private interface DoubleConsumer {
        void accept(double value);
    }

    @FunctionalInterface
    private interface FloatSupplier {
        float get();
    }

    @FunctionalInterface
    private interface FloatConsumer {
        void accept(float value);
    }
}