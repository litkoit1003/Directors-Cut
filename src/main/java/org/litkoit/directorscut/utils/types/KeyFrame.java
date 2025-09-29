package org.litkoit.directorscut.utils.types;

public record KeyFrame(double x, double y, double z, float xRot, float yRot, float fov, double timeLineSeconds, KeyFrameType keyFrameType) { }