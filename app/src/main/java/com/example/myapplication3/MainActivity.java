package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private OverlayCameraView overlayCameraView;

    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private Sensor gravitySensor;
    private Sensor accelerometerSensor;

    private OrientationListener orientationListener;
    private Camera camera;
    private SurfaceHolder surfaceHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        this.surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        this.overlayCameraView = (OverlayCameraView) findViewById(R.id.overlay_camera_view);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        this.accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.orientationListener = new OrientationListener(sensorManager, overlayCameraView);

        initCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magneticSensor != null) {
            sensorManager.registerListener(orientationListener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        if (gravitySensor != null) {
            sensorManager.registerListener(orientationListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
        }
        if (accelerometerSensor != null) {
            sensorManager.registerListener(orientationListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startCamera(holder, width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCamera() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    private void startCamera(SurfaceHolder sh, int width, int height) {
        Camera.Parameters parameters = camera.getParameters();
        List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size previewSizes = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
        Camera.Size pictureSizes = supportedPictureSizes.get(supportedPictureSizes.size() - 1);

        parameters.setPreviewSize(previewSizes.width, previewSizes.height);
        parameters.setPictureSize(pictureSizes.width, pictureSizes.height);
        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(sh);
        } catch (Exception e) {
            e.printStackTrace();
        }
        camera.setDisplayOrientation(90);
        camera.startPreview();
    }

    private void stopCamera() {
        surfaceHolder.removeCallback(this);
        camera.stopPreview();
        camera.release();
    }
}