package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    //TODO na ftiaksoume ta strings sta toast/biometric klp gia metafrasi se en
    //TODO allagi font sta agglika sto main activity
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
        iconSpinnerItems.add(new IconSpinnerItem("el_GR", getDrawable(R.drawable.greek_flag)));
        iconSpinnerItems.add(new IconSpinnerItem("en_GB", getDrawable(R.drawable.english_flag)));

        PowerSpinnerView spinnerView = findViewById(R.id.languageSpinner);
        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(spinnerView);
        spinnerView.setSpinnerAdapter(iconSpinnerAdapter);
        spinnerView.setItems(iconSpinnerItems);
        //update spinner using current locale or intent data (selected other language)
        Locale currentLocale =  getResources().getConfiguration().locale;
        Log.d("takis",currentLocale.toString());
        if(currentLocale.toString().startsWith("en_") || (getIntent().getExtras()!=null && getIntent().getExtras().getString("glwssa").startsWith("en_"))){
            spinnerView.selectItemByIndex(1);
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "fonts/NABILA.TTF");
            txtSlogan.setTypeface(face);
        }
        else if (currentLocale.toString().startsWith("el_") || getIntent().getExtras()!=null && getIntent().getExtras().getString("glwssa").startsWith("el_")){
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
                                        Toast.makeText(MainActivity.this, R.string.authFailed, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                //setup title,description on auth dialog
                                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                        .setTitle(getString(R.string.biometricAuth))
                                        .setSubtitle(getString(R.string.biometricTitle))
                                        .setNegativeButtonText(getString(R.string.biometricNo))
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
        if(spinnerSelection.equals("en_GB")){
            if(getResources().getConfiguration().locale.toString().startsWith("en")){
                return;
            }
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            refresh.putExtra("glwssa","en_GB");
            startActivity(refresh);
        }
        else if(spinnerSelection.equals("el_GR")){
            if(getResources().getConfiguration().locale.toString().startsWith("el")){
                return;
            }
            Locale locale = new Locale("el");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            refresh.putExtra("glwssa","el_GR");
            startActivity(refresh);
        }
    }
}