package com.zperkowski.doomgles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import min3d.core.RendererActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameMainActivity extends RendererActivity {
    private static String TAG = "GameMainActivity";

    private DoomGLRenderer renderer;
    private GLSurfaceView glSurfaceView;
    private Floor floor;
    private Wall wall1, wall2, wall3, wall4;
    private Bitmap texture;
    private SensorManager sensorManager;
    private int windowHight;
    private int windowWidth;
    private boolean touchStarted = false;
    private int touchPositionX = 0;
    private int touchPositionY = 0;
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

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        glSurfaceView.queueEvent(new Runnable(){
            public void run() {
                touchPositionX = (int)event.getX();
                touchPositionY = (int)event.getY();
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN)
                    touchStarted = true;
                else if (action == MotionEvent.ACTION_UP)
                    touchStarted = false;
            }});
        return true;
    }

    class DoomGLRenderer implements GLSurfaceView.Renderer, SensorEventListener {
        private float cameraX, cameraY, cameraZ;
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
            // Log.d(TAG, "DoomGLRenderer.onSensorChanged(SensorEvent event)");
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
            // Floor
            texture = BitmapFactory.decodeResource(getResources(), R.drawable.floor_texture);
            floor = new Floor(gl10, texture);
            texture.recycle();

            // Wall
            texture = BitmapFactory.decodeResource(getResources(), R.drawable.wall_texture);
            wall1 = new Wall(gl10, texture);
            wall2 = new Wall(gl10, texture);
            wall3 = new Wall(gl10, texture);
            wall4 = new Wall(gl10, texture);
            texture.recycle();

            // Camera
            cameraX = cameraY = 0.0f;
            cameraZ = -10.0f;
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            // Log.d(TAG, "DoomGLRenderer.onSurfaceChanged() Surface changed. Width=" + width + " Height=" + height);
            windowHight = height;
            windowWidth = width;
            gl10.glViewport(0, 0, width, height);
            displayRatio = (float) width / height;
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            gl10.glLoadIdentity();
            gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//            gl10.glFrustumf(-displayRatio, displayRatio, -1, 1, 1, 100);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            // Log.d(TAG, "DoomGLRenderer.onDrawFrame(GL10 gl10)");
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            gl10.glLoadIdentity();
            GLU.gluPerspective(gl10, 45.0f, displayRatio, 0.1f, 150.0f);
            gl10.glMultMatrixf(rotationMatrix, 0);      // Camera rotation
            gl10.glTranslatef(cameraX, cameraY, cameraZ);// Camera position
            if (touchStarted)
                touchedScreen(touchPositionX, touchPositionY);
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl10.glMatrixMode(GL10.GL_MODELVIEW);
            gl10.glLoadIdentity();
            drawAllModels(gl10);
        }

        void touchedScreen(int x, int y) {
            if (x > 0 && x < windowWidth * 0.3)
                renderer.moveCamera(-1);
            else if (x < windowWidth * 0.6)
                Log.d(TAG, "Shoot");
            else if (x > windowWidth * 0.6 && x < windowWidth)
                renderer.moveCamera(1);
        }

        void moveCamera(int direction) {
            float tmpX = cameraX + rotationMatrix[4] * 0.5f * Math.signum(direction);
            float tmpY = cameraY + rotationMatrix[6] * 0.5f * Math.signum(direction);
            if (tmpX > -49.9 && tmpX < 49.9
                    && tmpY > -49.9 && tmpY < 49.9) {
                cameraX = tmpX;
                cameraY = tmpY;
            }
        }
    }

    void drawAllModels(GL10 gl10) {
        floor.draw(gl10);
        gl10.glTranslatef(0.0f, 50.0f, 0.0f);
        wall1.draw(gl10);
        gl10.glTranslatef(0.0f, -100.0f, 0.0f);
        wall2.draw(gl10);
        gl10.glTranslatef(50.0f, 50.0f, 0.0f);
        gl10.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
        wall3.draw(gl10);
        gl10.glTranslatef(0.0f, 100.0f, 0.0f);
        wall4.draw(gl10);
    }
}
