package org.litkoit.directorscut.utils.types;

import java.util.List;

public record DetachedCamera(double x, double y, double z, float xRot, float yRot, float fov, int keybind, List<KeyFrame> keyframes) {
}