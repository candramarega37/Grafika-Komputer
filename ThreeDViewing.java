package com.grafkom.viewing3d;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ThreeDViewing {
    private long window;
    private final Camera3D camera = new Camera3D();
    private final PerspectiveProjection perspective = new PerspectiveProjection();
    private final ProjectionTaxonomyManager projection = new ProjectionTaxonomyManager();
    private final StereoAnaglyphRenderer stereo = new StereoAnaglyphRenderer();
    private final Mesh3D cube = new Mesh3D();
    private ShaderProgram shader;
    private ShaderProgram depthShader;
    private Texture2D texture;
    private ShadowMapFBO shadowMap;
    private boolean stereoEnabled;
    private boolean blinnPhong = true;
    private boolean smoothShading;
    private boolean depthTestEnabled = true;
    private boolean backFaceCullingEnabled;
    private boolean textureEnabled = true;
    private boolean nearestFiltering;
    private boolean shadowsEnabled = true;
    private boolean mousePressed;
    private double lastX, lastY;
    private int width = 1000, height = 700;

    public void run() {
        init();
        try {
            loop();
        } finally {
            cube.close();
            shader.close();
            depthShader.close();
            texture.close();
            shadowMap.close();
            glfwDestroyWindow(window);
            glfwTerminate();
        }
    }

    private void init() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        window = glfwCreateWindow(width, height, "3D Visual Realism - Phong / Blinn-Phong", 0, 0);
        if (window == 0) throw new IllegalStateException("OpenGL 3.3 window creation failed");
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.08f, 0.09f, 0.13f, 1.0f);
        shader = new ShaderProgram("/shaders/phong.vert", "/shaders/phong.frag");
        depthShader = new ShaderProgram("/shaders/depth.vert", "/shaders/depth.frag");
        texture = new Texture2D("/textures/checker.png");
        shadowMap = new ShadowMapFBO();
        cube.uploadCube();
        cube.uploadGround();

        glfwSetFramebufferSizeCallback(window, (w, newWidth, newHeight) -> {
            width = newWidth;
            height = Math.max(newHeight, 1);
            glViewport(0, 0, width, height);
        });
        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                mousePressed = action == GLFW_PRESS;
                if (mousePressed) {
                    double[] x = new double[1], y = new double[1];
                    glfwGetCursorPos(window, x, y);
                    lastX = x[0]; lastY = y[0];
                }
            }
        });
        glfwSetCursorPosCallback(window, (w, xpos, ypos) -> {
            if (mousePressed) {
                camera.rotate((float) (xpos - lastX), (float) (ypos - lastY));
                lastX = xpos; lastY = ypos;
            }
        });
        glfwSetScrollCallback(window, (w, xoffset, yoffset) -> camera.zoom((float) yoffset * 0.4f));
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (action != GLFW_PRESS) return;
            switch (key) {
                case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
                case GLFW_KEY_B -> {
                    blinnPhong = !blinnPhong;
                    System.out.println("Shading: " + (blinnPhong ? "Blinn-Phong" : "Phong"));
                }
                case GLFW_KEY_F -> {
                    smoothShading = !smoothShading;
                    System.out.println("Surface shading: " + (smoothShading ? "Smooth" : "Flat"));
                }
                case GLFW_KEY_Z -> {
                    depthTestEnabled = !depthTestEnabled;
                    if (depthTestEnabled) glEnable(GL_DEPTH_TEST);
                    else glDisable(GL_DEPTH_TEST);
                    System.out.println("Z-Buffer depth test: " + (depthTestEnabled ? "ON" : "OFF"));
                }
                case GLFW_KEY_C -> {
                    backFaceCullingEnabled = !backFaceCullingEnabled;
                    if (backFaceCullingEnabled) {
                        glEnable(GL_CULL_FACE);
                        glCullFace(GL_BACK);
                        glFrontFace(GL_CCW);
                    } else {
                        glDisable(GL_CULL_FACE);
                    }
                    System.out.println("Back-face culling: " + (backFaceCullingEnabled ? "ON" : "OFF"));
                }
                case GLFW_KEY_T -> {
                    textureEnabled = !textureEnabled;
                    System.out.println("Texture mapping: " + (textureEnabled ? "ON" : "OFF"));
                }
                case GLFW_KEY_I -> {
                    nearestFiltering = !nearestFiltering;
                    texture.setFiltering(nearestFiltering);
                    System.out.println("Texture filtering: " + (nearestFiltering ? "Nearest" : "Bilinear + mipmap"));
                }
                case GLFW_KEY_M -> {
                    shadowsEnabled = !shadowsEnabled;
                    System.out.println("Shadow mapping: " + (shadowsEnabled ? "ON" : "OFF"));
                }
                case GLFW_KEY_1 -> projection.setProjection(ProjectionTaxonomyManager.ProjectionType.ORTHOGRAPHIC);
                case GLFW_KEY_2 -> projection.setProjection(ProjectionTaxonomyManager.ProjectionType.ISOMETRIC);
                case GLFW_KEY_3 -> projection.setProjection(ProjectionTaxonomyManager.ProjectionType.OBLIQUE_CAVALIER);
                case GLFW_KEY_4 -> projection.setProjection(ProjectionTaxonomyManager.ProjectionType.PERSPECTIVE_1POINT);
                case GLFW_KEY_5 -> projection.setProjection(ProjectionTaxonomyManager.ProjectionType.PERSPECTIVE_2POINT);
                case GLFW_KEY_6 -> projection.setProjection(ProjectionTaxonomyManager.ProjectionType.PERSPECTIVE_3POINT);
                case GLFW_KEY_S -> stereoEnabled = !stereoEnabled;
                case GLFW_KEY_EQUAL, GLFW_KEY_KP_ADD -> perspective.setFovDegree(perspective.getFovDegree() - 5);
                case GLFW_KEY_MINUS, GLFW_KEY_KP_SUBTRACT -> perspective.setFovDegree(perspective.getFovDegree() + 5);
            }
        });
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            float aspect = (float) width / height;
            perspective.setAspectRatio(aspect);
            Matrix4f lightSpace = createLightSpaceMatrix();
            if (shadowsEnabled) renderShadowPass(lightSpace);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glViewport(0, 0, width, height);
            if (depthTestEnabled) glEnable(GL_DEPTH_TEST); else glDisable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            if (stereoEnabled) stereo.renderAnaglyph(eye -> renderScene(eye, lightSpace));
            else renderScene(0.0f, lightSpace);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private Matrix4f cubeModel() {
        Matrix4f model = new Matrix4f();
        switch (projection.getCurrentType()) {
            case ISOMETRIC -> model.rotateX((float) Math.toRadians(35.264f)).rotateY((float) Math.toRadians(45));
            case OBLIQUE_CAVALIER, PERSPECTIVE_2POINT -> model.rotateY((float) Math.toRadians(20));
            case PERSPECTIVE_3POINT -> model.rotateY((float) Math.toRadians(20)).rotateX((float) Math.toRadians(15));
            default -> { }
        }
        return model;
    }

    private Matrix4f createLightSpaceMatrix() {
        Matrix4f lightProjection = new Matrix4f().ortho(-8f, 8f, -8f, 8f, 1f, 20f);
        Matrix4f lightView = new Matrix4f().lookAt(-3.5f, 7.0f, 4.5f, 0f, 0f, 0f, 0f, 1f, 0f);
        return lightProjection.mul(lightView);
    }

    private void renderShadowPass(Matrix4f lightSpace) {
        glEnable(GL_DEPTH_TEST);
        shadowMap.bindForWriting();
        depthShader.use();
        setMatrix(depthShader, "lightSpaceMatrix", lightSpace);
        setMatrix(depthShader, "model", cubeModel());
        cube.drawCube();
        setMatrix(depthShader, "model", new Matrix4f());
        cube.drawGround();
        glUseProgram(0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        if (depthTestEnabled) glEnable(GL_DEPTH_TEST); else glDisable(GL_DEPTH_TEST);
    }

    private void renderScene(float eyeOffset, Matrix4f lightSpace) {
        Matrix4f view = camera.calculateLookAtMatrix().translate(eyeOffset, 0, 0);
        float aspect = (float) width / height;
        Matrix4f projectionMatrix;
        if (projection.getCurrentType() == ProjectionTaxonomyManager.ProjectionType.ORTHOGRAPHIC) {
            projectionMatrix = new Matrix4f().ortho(-3f * aspect, 3f * aspect, -3f, 3f, 0.1f, 50f);
        } else {
            projectionMatrix = perspective.calculateProjectionMatrix();
        }

        shader.use();
        setMatrix(shader, "model", cubeModel());
        setMatrix(shader, "view", view);
        setMatrix(shader, "projection", projectionMatrix);
        setMatrix(shader, "lightSpaceMatrix", lightSpace);
        glUniform3f(shader.uniformLocation("lightPos"), 2.5f, 3.5f, 3.0f);
        glUniform3f(shader.uniformLocation("viewPos"), camera.getEye().x, camera.getEye().y, camera.getEye().z);
        glUniform3f(shader.uniformLocation("lightColor"), 1.0f, 1.0f, 1.0f);
        glUniform3f(shader.uniformLocation("objectColor"), 0.85f, 0.42f, 0.20f);
        glUniform1i(shader.uniformLocation("useBlinnPhong"), blinnPhong ? 1 : 0);
        glUniform1i(shader.uniformLocation("useSmoothShading"), smoothShading ? 1 : 0);
        glUniform1i(shader.uniformLocation("useTexture"), textureEnabled ? 1 : 0);
        glUniform1i(shader.uniformLocation("useShadows"), shadowsEnabled ? 1 : 0);
        glUniform1i(shader.uniformLocation("surfaceTexture"), 0);
        glUniform1i(shader.uniformLocation("shadowMap"), 1);
        texture.bind(0);
        shadowMap.bindForReading(1);
        glUniform3f(shader.uniformLocation("objectColor"), 1.0f, 1.0f, 1.0f);
        cube.drawCube();
        setMatrix(shader, "model", new Matrix4f());
        glUniform3f(shader.uniformLocation("objectColor"), 0.85f, 0.85f, 0.85f);
        cube.drawGround();
        glUseProgram(0);
    }

    private void setMatrix(ShaderProgram target, String name, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);
            glUniformMatrix4fv(target.uniformLocation(name), false, buffer);
        }
    }

    public static void main(String[] args) {
        new ThreeDViewing().run();
    }
}
