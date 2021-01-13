package com.example.eticket;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnSignUp, btnButton, btnButton2;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnButton = (Button) findViewById(R.id.button);

        txtSlogan = (TextView) findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Calligraphy_Pen.ttf");
        txtSlogan.setTypeface(face, Typeface.BOLD);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Θέλετε να συνδεθείτε με το δακτυλικό σας αποτύπωμα;")
                        .setMessage("Πατήστε όχι για να συνδεθείτε με τα στοιχεία σας.")
                        .setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //init bio metric
                                Executor executor = ContextCompat.getMainExecutor(MainActivity.this);
                                BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                                    @Override
                                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                        super.onAuthenticationError(errorCode, errString);
                                        //error authenticating, stop tasks that requires auth
                                        Toast.makeText(MainActivity.this, "Πρόβλημα πιστοποίησης!: " + errString, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                        super.onAuthenticationSucceeded(result);
                                        //authentication succeed, continue tasts that requires auth
                                        Toast.makeText(MainActivity.this, "Η πιστοποίηση ολοκληρώθηκε", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, RouteActivity.class));
                                    }

                                    @Override
                                    public void onAuthenticationFailed() {
                                        super.onAuthenticationFailed();
                                        //failed authenticating, stop tasks that requires auth
                                        Toast.makeText(MainActivity.this, "Η πιστοποίηση απέτυχε", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                //setup title,description on auth dialog
                                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                        .setTitle("Biometric Authentication")
                                        .setSubtitle("Συνδεθείτε χρησιμοποιώντας το δακτυλικό σας αποτύπωμα")
                                        .setNegativeButtonText("Κωδικός Εφαρμογής")
                                        .build();

                                //handle authBtn click, start authentication
                                biometricPrompt.authenticate(promptInfo);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Οχι", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                        })
                        .setIcon(R.drawable.ic_fingerprint)
                        .show();
            }
        });

        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            }
        });

    }
}