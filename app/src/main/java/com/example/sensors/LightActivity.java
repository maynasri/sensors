package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.LinearLayout;
import android.widget.TextView;

public class LightActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView brightnessTextView;
    private HalfCircleProgressBar halfCircleProgressBar;
    private LinearLayout mainLayout;
    private final float MAX_LUX = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        brightnessTextView = findViewById(R.id.brightnessTextView);
        halfCircleProgressBar = findViewById(R.id.halfCircleProgressBar);
        mainLayout = findViewById(R.id.LightLayout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        if (lightSensor == null) {
            brightnessTextView.setText("Capteur de luminosité non disponible.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enregistrer le listener pour le capteur de luminosité
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Désenregistrer le listener pour économiser de la batterie
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            // Récupérer la valeur de luminosité actuelle en lux
            float lux = event.values[0];

            // Calculer le pourcentage de luminosité
            int brightnessPercentage = (int) ((lux / MAX_LUX) * 100);
            brightnessPercentage = Math.min(brightnessPercentage, 100); // Limiter à 100%

            // Afficher le pourcentage de luminosité
            brightnessTextView.setText("Luminosité : " + brightnessPercentage + "%");
            halfCircleProgressBar.setProgress(brightnessPercentage);

            if (brightnessPercentage < 30) {
                // Mode nuit
                mainLayout.setBackgroundResource(R.drawable.nightwindow);

            } else if (brightnessPercentage >= 30 && brightnessPercentage < 70) {
                // Mode idéal
                mainLayout.setBackgroundResource(R.drawable.day);

            } else {
                // Mode lumière du jour
                mainLayout.setBackgroundResource(R.drawable.morning);
            }
        }
    }
}
