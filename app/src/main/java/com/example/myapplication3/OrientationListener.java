package com.example.myapplication3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

public class OrientationListener implements SensorEventListener {
    float[] gravityValues = null;
    float[] accelerometerValues = null;
    float[] magneticFieldValues = null;

    private SensorManager sensorManager;
    private OverlayCameraView overlayCameraView;

    private static final Location userLocation = new Location("user");
    private static final Location objectLocation1 = new Location("object1");
    private static final Location objectLocation2 = new Location("object2");

    OrientationListener(SensorManager sensorManager, OverlayCameraView overlayCameraView) {
        this.sensorManager = sensorManager;
        this.overlayCameraView = overlayCameraView;

        // ul. Szczecińska
        userLocation.setLatitude(54.400969371780114);
        userLocation.setLongitude(18.58407542465554);

        // Katedra Oliwska
        objectLocation1.setLatitude(54.41120034916357);
        objectLocation1.setLongitude(18.557952056023346);

        // Molo w Brzeźnie
        objectLocation2.setLatitude(54.41543981781753);
        objectLocation2.setLongitude(18.625420838827008);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_GRAVITY:
                gravityValues = sensorEvent.values.clone();
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticFieldValues = sensorEvent.values.clone();
                break;

            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = sensorEvent.values.clone();
                break;
        }

        if (gravityValues != null && magneticFieldValues != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = sensorManager.getRotationMatrix(R, I, gravityValues, magneticFieldValues);
            if (success) {
                float orientation[] = new float[3];
                orientation = sensorManager.getOrientation(R, orientation);
                float azimuth = orientation[0];
                float pitch = orientation[1];
                float roll = orientation[2];
                float azimuthDegrees = (float) (azimuth * 180 / 3.14159);
                float pitchDegrees = (float) (pitch * 180 / 3.14159);
                float rollDegrees = (float) (roll * 180 / 3.14159);
                float [] orientationInDegrees = {azimuthDegrees, pitchDegrees, rollDegrees};

                float[] cameraVector = {0, 0, -1};
                float[] deviceVector = new float[3];        // Wektor obrotu urządzenia
                deviceVector[0] = R[0] * cameraVector[0] + R[1] * cameraVector[1] + R[2] * cameraVector[2];
                deviceVector[1] = R[3] * cameraVector[0] + R[4] * cameraVector[1] + R[5] * cameraVector[2];
                deviceVector[2] = R[6] * cameraVector[0] + R[7] * cameraVector[1] + R[8] * cameraVector[2];

                float angleBetweenObject1 = calculateAngleBetweenLocations(userLocation, objectLocation1, deviceVector);
                float angleBetweenObject2 = calculateAngleBetweenLocations(userLocation, objectLocation2, deviceVector);

                this.overlayCameraView.passData(orientationInDegrees, deviceVector, angleBetweenObject1, angleBetweenObject2);
                this.overlayCameraView.invalidate();
            }
        }
    }

    float calculateAngleBetweenLocations(Location location1, Location location2, float [] deviceVector){
        float angle1 = location1.bearingTo(location2);
        double angle1Radians = angle1 * Math.PI / 180;
        float [] direction1 = {(float) Math.sin(angle1Radians), (float) Math.cos(angle1Radians), 0f};
        return (float) (getAngle(direction1, deviceVector) * 180 / Math.PI);
    }

    float dotProduct(float[] a, float[] b) {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }

    float vectorMagnitude(float[] vec) {
        return (float) Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
    }

    float getAngle(float[] a, float b[]) {
        return (float) Math.acos(dotProduct(a, b) / (vectorMagnitude(a) * vectorMagnitude(b)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}