package com.zperkowski.doomgles;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameMainActivity extends AppCompatActivity {

    private static String TAG = "GameMainActivity";

    private GLSurfaceView glSurfaceView;

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
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            Log.d(TAG, "onSurfaceChanged Surface changed. Width=" + width + " Height=" + height);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {

        }
    }
}
