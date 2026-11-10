package com.grafkom.viewing3d;

import org.joml.Matrix4f;

public class PerspectiveProjection {

    private float fovDegree = 45.0f;
    private float aspectRatio = 16.0f / 9.0f;
    private float zNear = 0.1f;
    private float zFar = 100.0f;

    public Matrix4f calculateProjectionMatrix() {
        return new Matrix4f().perspective(
                (float) Math.toRadians(fovDegree),
                aspectRatio,
                zNear,
                zFar
        );
    }

    public void setFovDegree(float fovDegree) {
        this.fovDegree = Math.max(20.0f, Math.min(100.0f, fovDegree));
    }

    public void setAspectRatio(float aspectRatio) {
        if (aspectRatio > 0) {
            this.aspectRatio = aspectRatio;
        }
    }

    public float getFovDegree() {
        return fovDegree;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }
}