package com.grafkom.viewing3d;

import static org.lwjgl.opengl.GL11.*;

import java.util.function.Consumer;

public class StereoAnaglyphRenderer {

    private float ipd = 0.20f;

    public void setIpd(float ipd) {
        this.ipd = Math.max(0.01f, Math.min(0.5f, ipd));
    }

    public float getIpd() {
        return ipd;
    }

    public void renderAnaglyph(Consumer<Float> renderSceneCallback) {

        // LEFT EYE - RED CHANNEL
        glColorMask(true, false, false, true);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderSceneCallback.accept(ipd / 2.0f);

        // RIGHT EYE - CYAN CHANNEL
        glColorMask(false, true, true, true);
        glClear(GL_DEPTH_BUFFER_BIT);

        renderSceneCallback.accept(-ipd / 2.0f);

        // RESTORE NORMAL COLOR
        glColorMask(true, true, true, true);
    }
}