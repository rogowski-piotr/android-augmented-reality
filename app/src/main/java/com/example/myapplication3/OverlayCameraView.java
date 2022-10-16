package com.example.myapplication3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class OverlayCameraView extends View implements SensorEventListener {
    private Context context;
    float [] orientationDegreesData = null;
    float [] deviceVector = null;
    float angleLocation1;
    float angleLocation2;

    public OverlayCameraView(Context context) {
        super(context);
        this.context = context;
    }

    public OverlayCameraView(Context context, AttributeSet attribs) {
        super(context, attribs);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        int textSize = 60;
        int textGap = 20;
        paint.setARGB(255, 255, 255, 255);
        paint.setTextSize(textSize);

        // Orientacja urządzenia w stopniach
        canvas.drawText(String.format("azimuth = %.2f", this.orientationDegreesData[0]), 50, 50 , paint);
        canvas.drawText(String.format("pitch = %.2f", this.orientationDegreesData[1]), 50,  50 + textSize + textGap, paint);
        canvas.drawText(String.format("roll = %.2f", this.orientationDegreesData[2]), 50, 50 + 2 * textSize + 2 * textGap, paint);

        // Aktualna wartość wektoru obrotu urządzenia
        canvas.drawText(String.format("[%.2f %.2f %.2f]", this.deviceVector[0], this.deviceVector[1], this.deviceVector[2]), 50, 50 + 3 * textSize + 3 * textGap, paint);

        // Kąty obrotu względem obu lokalizacji
        canvas.drawText(String.format("Angle1 = %.2f°", this.angleLocation1), 50, 50 + 4 * textSize + 4 * textGap, paint);
        canvas.drawText(String.format("Angle2 = %.2f°", this.angleLocation2), 50, 50 + 5 * textSize + 5 * textGap, paint);

    }

    public void passData(float[] orientationDegreesData, float[] deviceVector, float angleLocation1, float angleLocation2) {
        this.orientationDegreesData = orientationDegreesData;
        this.deviceVector = deviceVector;
        this.angleLocation1 = angleLocation1;
        this.angleLocation2 = angleLocation2;
    }
}
