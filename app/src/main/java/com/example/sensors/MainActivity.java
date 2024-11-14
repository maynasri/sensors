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

    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executor = ContextCompat.getMainExecutor(this);

        // Associer le bouton à l'événement d'authentification biométrique
        Button biometricLoginButton = findViewById(R.id.biometricLoginBtn);
        biometricLoginButton.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(this);

            // Vérifier si l'empreinte digitale est disponible
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    == BiometricManager.BIOMETRIC_SUCCESS) {
                // Empreinte digitale est disponible
                authenticateWithFingerprint();
            }
            // Vérifier si la reconnaissance faciale est disponible
            else if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                    == BiometricManager.BIOMETRIC_SUCCESS) {
                // Reconnaissance faciale est disponible
                authenticateWithFaceRecognition();
            } else {
                Toast.makeText(this, "Aucune méthode biométrique disponible.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Méthode pour authentifier par empreinte digitale
    private void authenticateWithFingerprint() {
        // Créer un nouveau BiometricPrompt pour l'empreinte digitale
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(),
                                "Erreur d'authentification : " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        startActivity(new Intent(MainActivity.this, LightActivity.class));
                        finish();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Échec de l'authentification", Toast.LENGTH_SHORT).show();
                    }
                });

        // Créer PromptInfo spécifique à l'empreinte digitale
        BiometricPrompt.PromptInfo fingerprintPromptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentification par empreinte digitale")
                .setSubtitle("Utilisez votre empreinte digitale pour vous authentifier")
                .setDescription("Placez votre doigt sur le capteur.")
                .setNegativeButtonText("Annuler")
                .build();

        // Lancer l'authentification
        biometricPrompt.authenticate(fingerprintPromptInfo);
    }

    // Méthode pour authentifier par reconnaissance faciale
    private void authenticateWithFaceRecognition() {
        // Créer un nouveau BiometricPrompt pour la reconnaissance faciale
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(),
                                "Erreur d'authentification : " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        startActivity(new Intent(MainActivity.this, LightActivity.class));
                        finish();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Échec de l'authentification", Toast.LENGTH_SHORT).show();
                    }
                });

        // Créer PromptInfo spécifique à la reconnaissance faciale
        BiometricPrompt.PromptInfo faceRecognitionPromptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentification par reconnaissance faciale")
                .setSubtitle("Utilisez votre visage pour vous authentifier")
                .setDescription("Positionnez votre visage devant la caméra.")
                .setNegativeButtonText("Annuler")
                .build();

        // Lancer l'authentification
        biometricPrompt.authenticate(faceRecognitionPromptInfo);
    }
}
