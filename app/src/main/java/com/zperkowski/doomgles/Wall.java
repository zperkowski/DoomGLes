package com.zperkowski.doomgles;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zperkowski on 14/06/2017.
 */

class Wall {
    static final String TAG = "Wall";

    private int textureId;
    private FloatBuffer vertexBuffer;
    // (3 3D coords per point) + ( 2 2D coords for texture) * (4 points)
    private static final int VERTEXES_SIZE = (3 + 2) * 4;
    private float vertices[] = {
            -50.0f, 0.0f, 0.0f, 0.0f, 0.0f, // Left bottom
            -50.0f, 0.0f, 50.0f, 0.0f, 1.0f,  // Left up
            50.0f, 0.0f, 50.0f, 1.0f, 1.0f,   // Right up
            50.0f, 0.0f, 0.0f, 1.0f, 0.0f   // Right bottom
    };

    Wall(GL10 gl10, Bitmap texture) {
        int textureIds[] = new int[1];
        gl10.glGenTextures(1, textureIds, 0);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
        gl10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, 0);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(VERTEXES_SIZE * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.flip();
        this.textureId = textureIds[0];
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
