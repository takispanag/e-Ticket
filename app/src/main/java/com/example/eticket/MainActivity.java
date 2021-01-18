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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.StringValue;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //TODO na ftiaksoume ta strings sta toast/biometric klp gia metafrasi se en
    //TODO allagi font sta agglika sto main activtty
    Button btnSignIn, btnSignUp;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        txtSlogan = (TextView) findViewById(R.id.txtSlogan);

        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
        iconSpinnerItems.add(new IconSpinnerItem("Ελ", getDrawable(R.drawable.greek_flag)));
        iconSpinnerItems.add(new IconSpinnerItem("En", getDrawable(R.drawable.english_flag2)));

        PowerSpinnerView spinnerView = findViewById(R.id.languageSpinner);
        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(spinnerView);
        spinnerView.setSpinnerAdapter(iconSpinnerAdapter);
        spinnerView.setItems(iconSpinnerItems);
        //update spinner using current locale or intent data (selected other language)
        Locale currentLocale =  getResources().getConfiguration().locale;
        Log.d("takis",currentLocale.toString());
        if(currentLocale.toString().startsWith("en") || (getIntent().getExtras()!=null && getIntent().getExtras().getString("glwssa").equals("en"))){
            spinnerView.selectItemByIndex(1);
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "fonts/NABILA.TTF");
            txtSlogan.setTypeface(face);
        }
        else if (currentLocale.toString().equals("el_GR") || getIntent().getExtras()!=null && getIntent().getExtras().getString("glwssa").equals("el")){
            spinnerView.selectItemByIndex(0);
        }

        //listener toy spinner gia allagi glwssas
        spinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<IconSpinnerItem>(){
            @Override
            public void onItemSelected(int oldIndex, @Nullable IconSpinnerItem oldIconSpinnerItem, int newIndex, IconSpinnerItem newIconSpinnerItem) {
                updateLanguage(newIconSpinnerItem.getText().toString());
            }
        });
        spinnerView.setLifecycleOwner(this);


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
                        .setTitle(R.string.fingerprintTitle)
                        .setMessage(R.string.fingerprintMessage)
                        .setPositiveButton(R.string.nai, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //init bio metric
                                Executor executor = ContextCompat.getMainExecutor(MainActivity.this);
                                BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                                    @Override
                                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                        super.onAuthenticationError(errorCode, errString);
                                        //error authenticating, stop tasks that requires auth
                                        Toast.makeText(MainActivity.this, getString(R.string.authProblem) + "\n"+errString, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                        super.onAuthenticationSucceeded(result);
                                        //authentication succeed, continue tasts that requires auth
                                        Toast.makeText(MainActivity.this, getString(R.string.authOk), Toast.LENGTH_SHORT).show();
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
                        .setNegativeButton(R.string.oxi, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                        })
                        .setIcon(R.drawable.ic_fingerprint)
                        .show();
            }
        });
    }

    public void updateLanguage(String spinnerSelection){
        if(spinnerSelection.equals("En")){
            if(getResources().getConfiguration().locale.toString().startsWith("en")){
                return;
            }
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            refresh.putExtra("glwssa","en");
            startActivity(refresh);
        }
        else if(spinnerSelection.equals("Ελ")){
            if(getResources().getConfiguration().locale.toString().startsWith("el")){
                return;
            }
            Locale locale = new Locale("el");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            refresh.putExtra("glwssa","el");
            startActivity(refresh);
        }
    }
}