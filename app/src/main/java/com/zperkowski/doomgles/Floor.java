package com.zperkowski.doomgles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zperkowski on 20/04/2017.
 */

class Floor {
    private int textureId;
    private FloatBuffer vertexBuffer;
    // (3 3D coords per point) + ( 2 2D coords for texture) * (4 points)
    private static final int VERTEXES_SIZE = (3 + 2) * 4;
    private float vertices[] = {
            -50.0f, 0.0f, -50.0f, 0.0f, 1.0f, // Left bottom
            50.0f, 0.0f, -50.0f, 0.0f, 0.0f,  // Left up
            50.0f, 0.0f, 50.0f, 1.0f, 0.0f,   // Right up
            -50.0f, 0.0f, 50.0f, 1.0f, 1.0f   // Right bottom
    };

    Floor(int textureId) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(Floor.VERTEXES_SIZE * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.flip();
        this.textureId = textureId;
    }

    void draw(GL10 gl) {
        // Enable textures on this object
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // Bindes the object to a given texture id
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        // Point to our vertex buffer
        vertexBuffer.position(0);
        gl.glVertexPointer(3, GL10.GL_FLOAT, VERTEXES_SIZE, vertexBuffer);
        vertexBuffer.position(3);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, VERTEXES_SIZE, vertexBuffer);
        // Draw the vertices as triangle strip
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
        // Disable the client state before leaving
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }
}
