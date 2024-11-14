package com.example.sensors;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Définir un exécuteur
        executor = ContextCompat.getMainExecutor(this);

        // Initialiser le BiometricPrompt
        biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(),
                                        "Erreur d'authentification : " + errString, Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Log.d("Biometric", "Authentication succeeded! Starting LightActivity...");
                        try {
                            Intent intent = new Intent(MainActivity.this, LightActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Authentification avec succès!", Toast.LENGTH_SHORT).show();

                            // Utiliser un délai pour finir MainActivity après l'ouverture de LightActivity
                            new Handler().postDelayed(() -> finish(), 100);  // Délai de 100ms
                        } catch (Exception e) {
                            Log.e("Biometric", "Error starting LightActivity: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentification échouée",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        // Créer les informations de l'invite (le message à afficher à l'utilisateur)
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentification biométrique")
                .setSubtitle("Choisissez la méthode d'authentification biométrique disponible ")
                .setNegativeButtonText("Annuler")
                .build();

        // Associer le bouton à l'événement d'authentification par reconnaissance faciale
        Button faceRecognitionLoginButton = findViewById(R.id.faceRecognitionLoginBtn);
        faceRecognitionLoginButton.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    // L'authentification par reconnaissance faciale peut être lancée
                    biometricPrompt.authenticate(promptInfo);
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Toast.makeText(this, "No facial recognition hardware available", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Toast.makeText(this, "Facial recognition hardware is currently unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    Toast.makeText(this, "No face recognition data enrolled. Please set up face recognition in settings", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
