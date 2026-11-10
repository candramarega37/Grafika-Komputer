package com.grafkom.viewing3d;

import static org.lwjgl.opengl.GL11.*;

public class ProjectionTaxonomyManager {

    public enum ProjectionType {
        ORTHOGRAPHIC,
        ISOMETRIC,
        OBLIQUE_CAVALIER,
        PERSPECTIVE_1POINT,
        PERSPECTIVE_2POINT,
        PERSPECTIVE_3POINT
    }

    private ProjectionType currentType = ProjectionType.PERSPECTIVE_1POINT;

    public void setProjection(ProjectionType type) {
        currentType = type;
    }

    public ProjectionType getCurrentType() {
        return currentType;
    }

    public void applyProjection(float aspect) {

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        switch (currentType) {

            case ORTHOGRAPHIC:
                glOrtho(
                        -3.0 * aspect, 3.0 * aspect,
                        -3.0, 3.0,
                        0.1, 50.0
                );
                break;

            case ISOMETRIC:
                glOrtho(
                        -3.0 * aspect, 3.0 * aspect,
                        -3.0, 3.0,
                        0.1, 50.0
                );
                break;

            case OBLIQUE_CAVALIER:
                glOrtho(
                        -3.0 * aspect, 3.0 * aspect,
                        -3.0, 3.0,
                        0.1, 50.0
                );
                break;

            case PERSPECTIVE_1POINT:
                glFrustum(
                        -aspect * 0.1, aspect * 0.1,
                        -0.1, 0.1,
                        0.1, 50.0
                );
                break;

            case PERSPECTIVE_2POINT:
                glFrustum(
                        -aspect * 0.1, aspect * 0.1,
                        -0.1, 0.1,
                        0.1, 50.0
                );
                break;

            case PERSPECTIVE_3POINT:
                glFrustum(
                        -aspect * 0.1, aspect * 0.1,
                        -0.1, 0.1,
                        0.1, 50.0
                );
                break;
        }

        glMatrixMode(GL_MODELVIEW);
    }

    public void applyModelTransform() {

        switch (currentType) {

            case ISOMETRIC:
                glRotatef(35.264f, 1, 0, 0);
                glRotatef(45.0f, 0, 1, 0);
                break;

            case OBLIQUE_CAVALIER:
                glRotatef(30.0f, 0, 1, 0);
                break;

            case PERSPECTIVE_2POINT:
                glRotatef(20.0f, 0, 1, 0);
                break;

            case PERSPECTIVE_3POINT:
                glRotatef(20.0f, 0, 1, 0);
                glRotatef(15.0f, 1, 0, 0);
                break;

            default:
                break;
        }
    }
}