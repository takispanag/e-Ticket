package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnSignUp, btnButton;
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
//        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Calligraphy_Pen.ttf");
//        txtSlogan.setTypeface(face, Typeface.BOLD);


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
                                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

    }
}