package com.zperkowski.doomgles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

    private DoomGLRenderer renderer;
    private GLSurfaceView glSurfaceView;
    private Floor floor;
    private Bitmap floorTexture;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(Bundle savedInstanceState()");
        super.onCreate(savedInstanceState);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Sets OpenGL environment
        renderer = new DoomGLRenderer();
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setRenderer(renderer);
        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        renderer.start();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");

        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        renderer.stop();
        glSurfaceView.onPause();
    }

    class DoomGLRenderer implements GLSurfaceView.Renderer, SensorEventListener {
        float displayRatio;
        private Sensor rotationVectorSensor;
        private final float[] rotationMatrix = new float[16];

        DoomGLRenderer() {
            Log.d(TAG, "DoomGLRenderer()");
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            rotationMatrix[ 0] = 1;
            rotationMatrix[ 4] = 1;
            rotationMatrix[ 8] = 1;
            rotationMatrix[12] = 1;
        }

        void start() {
            Log.d(TAG, "DoomGLRenderer.start()");
            // enable our sensor when the activity is resumed, ask for
            // 10 ms updates.
            sensorManager.registerListener(this, rotationVectorSensor, 10000);
        }

        void stop() {
            Log.d(TAG, "DoomGLRenderer.stop()");
            // make sure to turn our sensor off when the activity is paused
            sensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "DoomGLRenderer.onSensorChanged(SensorEvent event)");
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // convert the rotation-vector to a 4x4 matrix. the matrix
                // is interpreted by Open GL as the inverse of the
                // rotation-vector, which is what we want.
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "DoomGLRenderer.onAccuracyChanged");
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            Log.d(TAG,  "DoomGLRenderer.onSurfaceCreated() Surface created");
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
            Log.d(TAG, "DoomGLRenderer.onSurfaceChanged() Surface changed. Width=" + width + " Height=" + height);
            gl10.glViewport(0, 0, width, height);
            displayRatio = (float) width / height;
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            gl10.glLoadIdentity();
//            gl10.glFrustumf(-displayRatio, displayRatio, -1, 1, 1, 100);
            GLU.gluPerspective(gl10, 45.0f, displayRatio, 0.1f, 100.0f);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            Log.d(TAG, "DoomGLRenderer.onDrawFrame(GL10 gl10)");
            gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl10.glMatrixMode(GL10.GL_MODELVIEW);
            gl10.glLoadIdentity();
            gl10.glTranslatef(0.0f, -2.0f, -30.0f);
            gl10.glMultMatrixf(rotationMatrix, 0);
            floor.draw(gl10);
        }
    }
}
