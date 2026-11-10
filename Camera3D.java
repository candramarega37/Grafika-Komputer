package com.grafkom.viewing3d;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera3D {

    private Vector3f eye;
    private Vector3f target;
    private Vector3f up;

    private float radius = 6.0f;
    private float azimuth = 45.0f;
    private float elevation = 25.0f;

    public Camera3D() {
        target = new Vector3f(0, 0, 0);
        up = new Vector3f(0, 1, 0);
        eye = new Vector3f();
        updatePosition();
    }

    public void updatePosition() {
        float az = (float) Math.toRadians(azimuth);
        float el = (float) Math.toRadians(elevation);

        eye.x = radius * (float) (Math.cos(el) * Math.sin(az));
        eye.y = radius * (float) Math.sin(el);
        eye.z = radius * (float) (Math.cos(el) * Math.cos(az));
    }

    public void rotate(float dx, float dy) {
        azimuth += dx * 0.5f;
        elevation -= dy * 0.5f;

        elevation = Math.max(-85, Math.min(85, elevation));
        updatePosition();
    }

    public void zoom(float amount) {
        radius -= amount;
        radius = Math.max(2.5f, Math.min(15.0f, radius));
        updatePosition();
    }

    public Matrix4f calculateLookAtMatrix() {
        return new Matrix4f().lookAt(eye, target, up);
    }

    public Vector3f getEye() {
        return new Vector3f(eye);
    }

    public Vector3f getTarget() {
        return new Vector3f(target);
    }

    public Vector3f getUp() {
        return new Vector3f(up);
    }
}