package com.zperkowski.doomgles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameMainActivity extends AppCompatActivity {

    private static String TAG = "GameMainActivity";

    private GLSurfaceView glSurfaceView;
    private Floor floor;
    private Bitmap floorTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Sets OpenGL environment
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setRenderer(new DoomGLRenderer());
        setContentView(glSurfaceView);

    }

    class DoomGLRenderer implements GLSurfaceView.Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            Log.d(TAG,  "onSurfaceCreated Surface created");
            floorTexture = BitmapFactory.decodeResource(getResources(), R.drawable.floor_texture);
            Log.d(TAG, floorTexture.toString());
            int textureIds[] = new int[1];
            gl10.glGenTextures(1, textureIds, 0);
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, floorTexture, 0);
            gl10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, 0);
            floorTexture.recycle();
            floor = new Floor(textureIds[0]);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            Log.d(TAG, "onSurfaceChanged Surface changed. Width=" + width + " Height=" + height);
            gl10.glViewport(0, 0, width, height);
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            gl10.glLoadIdentity();
            GLU.gluPerspective(gl10, 45.0f, (float) width / (float) height, 0.1f, 50.0f);
            gl10.glMatrixMode(GL10.GL_MODELVIEW);
            gl10.glLoadIdentity();
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl10.glLoadIdentity();
            gl10.glTranslatef(0.0f, -2.0f, -30.0f);    // Sets camera position
            floor.draw(gl10);
        }
    }
}
