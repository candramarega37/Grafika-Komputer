package com.grafkom.viewing3d;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/** Cube and ground meshes. Vertex layout: position, smooth normal, face normal, UV. */
public class Mesh3D implements AutoCloseable {
    private static final int STRIDE = 11 * Float.BYTES;
    private int cubeVao, cubeVbo, groundVao, groundVbo;

    public void uploadCube() {
        float[] data = new float[36 * 11];
        int[] cursor = {0};
        addFace(data, cursor, new float[]{0,0,1}, new float[][]{{-1,-1,1},{1,-1,1},{1,1,1},{-1,-1,1},{1,1,1},{-1,1,1}});
        addFace(data, cursor, new float[]{0,0,-1}, new float[][]{{1,-1,-1},{-1,-1,-1},{-1,1,-1},{1,-1,-1},{-1,1,-1},{1,1,-1}});
        addFace(data, cursor, new float[]{0,1,0}, new float[][]{{-1,1,1},{1,1,1},{1,1,-1},{-1,1,1},{1,1,-1},{-1,1,-1}});
        addFace(data, cursor, new float[]{0,-1,0}, new float[][]{{-1,-1,-1},{1,-1,-1},{1,-1,1},{-1,-1,-1},{1,-1,1},{-1,-1,1}});
        addFace(data, cursor, new float[]{1,0,0}, new float[][]{{1,-1,1},{1,-1,-1},{1,1,-1},{1,-1,1},{1,1,-1},{1,1,1}});
        addFace(data, cursor, new float[]{-1,0,0}, new float[][]{{-1,-1,-1},{-1,-1,1},{-1,1,1},{-1,-1,-1},{-1,1,1},{-1,1,-1}});
        int[] handles = upload(data);
        cubeVao = handles[0]; cubeVbo = handles[1];
    }

    public void uploadGround() {
        float y = -1.06f;
        float[] data = new float[6 * 11];
        int[] cursor = {0};
        float[][] points = {{-6,y,-6},{-6,y,6},{6,y,6},{-6,y,-6},{6,y,6},{6,y,-6}};
        for (float[] p : points) {
            data[cursor[0]++] = p[0]; data[cursor[0]++] = p[1]; data[cursor[0]++] = p[2];
            data[cursor[0]++] = 0; data[cursor[0]++] = 1; data[cursor[0]++] = 0;
            data[cursor[0]++] = 0; data[cursor[0]++] = 1; data[cursor[0]++] = 0;
            data[cursor[0]++] = (p[0] + 6) / 2; data[cursor[0]++] = (p[2] + 6) / 2;
        }
        int[] handles = upload(data);
        groundVao = handles[0]; groundVbo = handles[1];
    }

    private static void addFace(float[] data, int[] cursor, float[] normal, float[][] points) {
        for (float[] p : points) {
            data[cursor[0]++] = p[0]; data[cursor[0]++] = p[1]; data[cursor[0]++] = p[2];
            float length = (float)Math.sqrt(p[0]*p[0] + p[1]*p[1] + p[2]*p[2]);
            data[cursor[0]++] = p[0]/length; data[cursor[0]++] = p[1]/length; data[cursor[0]++] = p[2]/length;
            data[cursor[0]++] = normal[0]; data[cursor[0]++] = normal[1]; data[cursor[0]++] = normal[2];
            if (Math.abs(normal[1]) > 0.5f) {
                data[cursor[0]++] = (p[0] + 1) / 2; data[cursor[0]++] = (p[2] + 1) / 2;
            } else if (Math.abs(normal[0]) > 0.5f) {
                data[cursor[0]++] = (p[2] + 1) / 2; data[cursor[0]++] = (p[1] + 1) / 2;
            } else {
                data[cursor[0]++] = (p[0] + 1) / 2; data[cursor[0]++] = (p[1] + 1) / 2;
            }
        }
    }

    private static int[] upload(float[] values) {
        FloatBuffer data = BufferUtils.createFloatBuffer(values.length);
        data.put(values).flip();
        int vao = glGenVertexArrays(), vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, STRIDE, 0L); glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, STRIDE, 3L * Float.BYTES); glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, STRIDE, 6L * Float.BYTES); glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, 2, GL_FLOAT, false, STRIDE, 9L * Float.BYTES); glEnableVertexAttribArray(3);
        glBindBuffer(GL_ARRAY_BUFFER, 0); glBindVertexArray(0);
        return new int[]{vao, vbo};
    }

    public void drawCube() { glBindVertexArray(cubeVao); glDrawArrays(GL_TRIANGLES, 0, 36); glBindVertexArray(0); }
    public void drawGround() { glBindVertexArray(groundVao); glDrawArrays(GL_TRIANGLES, 0, 6); glBindVertexArray(0); }

    @Override public void close() {
        if (cubeVbo != 0) glDeleteBuffers(cubeVbo);
        if (cubeVao != 0) glDeleteVertexArrays(cubeVao);
        if (groundVbo != 0) glDeleteBuffers(groundVbo);
        if (groundVao != 0) glDeleteVertexArrays(groundVao);
    }
}
