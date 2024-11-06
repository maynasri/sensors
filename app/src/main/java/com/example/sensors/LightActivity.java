package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LightActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView brightnessTextView;
    private ProgressBar brightnessProgressBar;
    private LinearLayout mainLayout;
    private final float MAX_LUX = 10000; // Valeur de référence maximale de luminosité en lux

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        // Initialisation des éléments de l'interface utilisateur
        brightnessTextView = findViewById(R.id.brightnessTextView);
        brightnessProgressBar = findViewById(R.id.brightnessProgressBar);
        mainLayout = findViewById(R.id.LightLayout);

        // Initialisation du gestionnaire de capteurs
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        // Vérification de la disponibilité du capteur
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
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            // Récupérer la valeur de luminosité actuelle en lux
            float lux = event.values[0];

            // Calculer le pourcentage de luminosité
            int brightnessPercentage = (int) ((lux / MAX_LUX) * 100);
            brightnessPercentage = Math.min(brightnessPercentage, 100); // Limiter à 100%

            // Afficher le pourcentage de luminosité
            brightnessTextView.setText("Luminosité : " + brightnessPercentage + "%");
            brightnessProgressBar.setProgress(brightnessPercentage);

            // Changer le thème selon le niveau de luminosité
            if (brightnessPercentage < 30) {
                // Mode nuit
                mainLayout.setBackgroundColor(Color.parseColor("#121212")); // Couleur sombre
                brightnessTextView.setTextColor(Color.parseColor("#FFFFFF")); // Texte clair
                brightnessProgressBar.setProgressTintList(getResources().getColorStateList(android.R.color.holo_blue_light));
            } else if (brightnessPercentage >= 30 && brightnessPercentage < 70) {
                // Mode idéal
                mainLayout.setBackgroundColor(Color.parseColor("#FFDD94")); // Couleur douce
                brightnessTextView.setTextColor(Color.parseColor("#333333")); // Texte foncé
                brightnessProgressBar.setProgressTintList(getResources().getColorStateList(android.R.color.holo_orange_dark));
            } else {
                // Mode lumière du jour
                mainLayout.setBackgroundColor(Color.parseColor("#FFFFFF")); // Couleur claire
                brightnessTextView.setTextColor(Color.parseColor("#000000")); // Texte foncé
                brightnessProgressBar.setProgressTintList(getResources().getColorStateList(android.R.color.holo_orange_light));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Méthode requise pour l'interface SensorEventListener, mais non utilisée ici
    }
}
