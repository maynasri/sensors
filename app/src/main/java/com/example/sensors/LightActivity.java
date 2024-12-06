package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LightActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView brightnessTextView;
    private View progressBar;
    private View lampBulb;
    private View lightHalo;
    private ImageView lightModeIcon;
    private View gradientBackground;
    private Button logoutButton;
    private final float MAX_LUX = 10000f;
    private static final long ANIMATION_DURATION = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        // Initialize views
        brightnessTextView = findViewById(R.id.brightnessTextView);
        progressBar = findViewById(R.id.progressBar);
        lampBulb = findViewById(R.id.lampBulb);
        lightHalo = findViewById(R.id.lightHalo);
        lightModeIcon = findViewById(R.id.lightModeIcon);
        gradientBackground = findViewById(R.id.gradientBackground);
        logoutButton = findViewById(R.id.logoutButton);

        // Set up logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        // Check sensor availability
        if (lightSensor == null) {
            brightnessTextView.setText("Light sensor not available");
        }

        // Set initial state
        updateLightState(50f); // Default state
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            updateLightState(lux);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used but required by interface
    }

    private void updateLightState(float lux) {
        // Calculate brightness percentage
        int brightnessPercentage = (int) Math.min((lux / MAX_LUX) * 100, 100);

        // Update brightness text
        brightnessTextView.setText(String.format("Brightness: %d%%", brightnessPercentage));

        // Update progress bar width
        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
        int parentWidth = ((View) progressBar.getParent()).getWidth();
        if (parentWidth > 0) {
            params.width = (int) ((parentWidth - 32) * (brightnessPercentage / 100f));
            progressBar.setLayoutParams(params);
        }

        // Determine colors and resources based on brightness
        int bulbColor, haloColor, progressColor;
        int backgroundRes, iconRes;

        if (brightnessPercentage < 30) {
            bulbColor = ContextCompat.getColor(this, R.color.bulb_night);
            haloColor = ContextCompat.getColor(this, R.color.halo_night);
            progressColor = ContextCompat.getColor(this, R.color.progress_night);
            backgroundRes = R.drawable.gradient_night;
            iconRes = R.drawable.ic_moon;
        } else if (brightnessPercentage < 70) {
            bulbColor = ContextCompat.getColor(this, R.color.bulb_evening);
            haloColor = ContextCompat.getColor(this, R.color.halo_evening);
            progressColor = ContextCompat.getColor(this, R.color.progress_evening);
            backgroundRes = R.drawable.gradient_evening;
            iconRes = R.drawable.ic_sunset;
        } else {
            bulbColor = ContextCompat.getColor(this, R.color.bulb_day);
            haloColor = ContextCompat.getColor(this, R.color.halo_day);
            progressColor = ContextCompat.getColor(this, R.color.progress_day);
            backgroundRes = R.drawable.gradient_day;
            iconRes = R.drawable.ic_sun;
        }

        // Update lamp colors with animation
        setLampColor(bulbColor, haloColor);
        animateProgressBarColor(progressColor);

        gradientBackground.setBackgroundResource(backgroundRes);
        lightModeIcon.setImageResource(iconRes);

        float alpha = brightnessPercentage / 100f;
        lampBulb.animate()
                .alpha(0.3f + (0.7f * alpha))
                .setDuration(ANIMATION_DURATION)
                .start();
        lightHalo.animate()
                .alpha(alpha)
                .setDuration(ANIMATION_DURATION)
                .start();
    }

    private void setLampColor(int bulbColor, int haloColor) {
        GradientDrawable bulbDrawable = new GradientDrawable();
        bulbDrawable.setShape(GradientDrawable.OVAL);
        bulbDrawable.setColor(bulbColor);
        bulbDrawable.setStroke(4, Color.WHITE);
        lampBulb.setBackground(bulbDrawable);

        GradientDrawable haloDrawable = new GradientDrawable();
        haloDrawable.setShape(GradientDrawable.OVAL);
        haloDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        haloDrawable.setGradientRadius(dpToPx(96));
        haloDrawable.setColors(new int[]{haloColor, Color.TRANSPARENT});
        lightHalo.setBackground(haloDrawable);
    }

    private void animateProgressBarColor(int toColor) {
        GradientDrawable background = (GradientDrawable) progressBar.getBackground();
        int fromColor = ContextCompat.getColor(this, R.color.progress_background);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnimation.setDuration(ANIMATION_DURATION);
        colorAnimation.addUpdateListener(animator -> background.setColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
