package com.grafkom.viewing3d;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.lwjgl.opengl.GL20;

/** Loads and manages a GLSL vertex/fragment shader pair from the classpath. */
public final class ShaderProgram implements AutoCloseable {

    private final int programId;

    public ShaderProgram(String vertexResource, String fragmentResource) {
        int vertexId = compile(GL20.GL_VERTEX_SHADER, readResource(vertexResource));
        int fragmentId = compile(GL20.GL_FRAGMENT_SHADER, readResource(fragmentResource));
        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexId);
        GL20.glAttachShader(programId, fragmentId);
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(programId);
            GL20.glDeleteProgram(programId);
            GL20.glDeleteShader(vertexId);
            GL20.glDeleteShader(fragmentId);
            throw new IllegalStateException("Shader link failed: " + log);
        }
        GL20.glDetachShader(programId, vertexId);
        GL20.glDetachShader(programId, fragmentId);
        GL20.glDeleteShader(vertexId);
        GL20.glDeleteShader(fragmentId);
    }

    private static String readResource(String path) {
        try (InputStream in = ShaderProgram.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Shader resource not found: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot read shader resource: " + path, ex);
        }
    }

    private static int compile(int type, String source) {
        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shaderId);
            GL20.glDeleteShader(shaderId);
            throw new IllegalStateException("Shader compilation failed: " + log);
        }
        return shaderId;
    }

    public void use() {
        GL20.glUseProgram(programId);
    }

    public int uniformLocation(String name) {
        return GL20.glGetUniformLocation(programId, name);
    }

    public int id() {
        return programId;
    }

    @Override
    public void close() {
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(programId);
    }
}
