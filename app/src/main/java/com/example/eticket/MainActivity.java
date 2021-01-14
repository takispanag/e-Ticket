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
import android.os.Build;
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
    CollectionReference dbSeats = FirebaseFirestore.getInstance().collection("UserSeats");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<String> myUserRoutes = new ArrayList<>();
    List<String> simerinaRoute = new ArrayList<>();

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

        Executors.newSingleThreadExecutor().execute(() -> {
            dbSeats.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            for ( String key : document.getData().keySet() ) {
                                Log.d("lista2 klidi",key);
                                myUserRoutes.add(key);
                            }
                            //notification 30 lepta prin tin anaxwrisi
                            split("",myUserRoutes,"SimerinaDromologia");//gemizw simerina route list
                            Log.d("Lista2 ",simerinaRoute.toString());

                //            //paradeigma
                //            String s = "14-01-2021 ΑΜΑΛΙΑΔΑ-ΑΚΡΑΤΑ 21:31";
                //
                //            String wra = "";
                //            String imerominia = "";
                //            String[] part = s.split(" "); //returns an array with the 3 parts
                //            imerominia = part[0]; // imerominia
                //            wra = part[2]; // ora

                            //notification if 30 minutes before departure
                            while (true){
                                for(int i = 0;i<simerinaRoute.size();i++){
                                    List<String> dromologioMeraWra = split(simerinaRoute.get(i), Collections.emptyList(),"Dromologio");
                                    Log.d("DROMOLOGIO mera:wra",dromologioMeraWra.get(0)+":"+dromologioMeraWra.get(1));
                                    List<String> twriniMeraWra = split("",Collections.emptyList(),"TwriniWra");
                                    Log.d("TWRINI mera:wra",twriniMeraWra.get(0)+":"+twriniMeraWra.get(1));

                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                    Date time_now = null;
                                    Date wra_anaxwrisis = null;
                                    try {
                                        time_now = format.parse(twriniMeraWra.get(1));
                                        wra_anaxwrisis = format.parse(dromologioMeraWra.get(1));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long diff = wra_anaxwrisis.getTime() - time_now.getTime();
                                    int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(diff);

                                    if(dromologioMeraWra.get(0).equalsIgnoreCase(twriniMeraWra.get(0)) && minutes<=30 && minutes>=0) {
                                        int NOTIFICATION_ID = 234;
                                        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                        String CHANNEL_ID = "";
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            CHANNEL_ID = "my_channel_01";
                                            CharSequence name = "my_channel";
                                            String Description = "This is my channel";
                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                            mChannel.setDescription(Description);
                                            mChannel.enableLights(true);
                                            mChannel.setLightColor(Color.RED);
                                            mChannel.enableVibration(true);
                                            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                            mChannel.setShowBadge(false);
                                            notificationManager.createNotificationChannel(mChannel);
                                        }

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("TITLE")
                                                .setContentText("message");

                                        Intent resultIntent = new Intent(getBaseContext(), ProfileActivity.class);
                                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                                        stackBuilder.addParentStack(MainActivity.class);
                                        stackBuilder.addNextIntent(resultIntent);
                                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                        builder.setContentIntent(resultPendingIntent);
                                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                                    }
                                }
                                try {
                                    Thread.sleep(60000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d("theseis ", "No such document");
                        }
                    } else {
                        Log.d("theseis ", "get failed with ", task.getException());
                    }
                }
            });
        });

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
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

    }

    public List<String> split(String myRoute, List<String> myUserRoutes, String type){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        List<String> meraWraList = new ArrayList<>();
        if(type.equals("Dromologio")){
            String wra = "", imerominia = "";
            String[] part = myRoute.split(" "); //returns an array with the 3 parts
            imerominia = part[0]; // imerominia
            wra = part[1]; // ora
            Log.d("DROMOLOGIO Imera:wra",imerominia+":"+wra);
            meraWraList.add(imerominia);
            meraWraList.add(wra);
        }
        else if(type.equals("TwriniWra")){
            LocalDateTime now = LocalDateTime.now();
            String[] date = dtf.format(now).split(" ");
            String imerominia = date[0]; //imerominia twra
            String wra = date[1]; //wra twra
            Log.d("TWRINI WRA Imera:wra",imerominia+":"+wra);
            meraWraList.add(imerominia);
            meraWraList.add(wra);
        }
        else if(type.equals("SimerinaDromologia")){
            for(int i = 0 ;i<myUserRoutes.size();i++) {
                String wra = "", imerominia = "";
                String myRouteLocal = myUserRoutes.get(i);
                String[] part = myRouteLocal.split(" "); //returns an array with the 3 parts
                imerominia = part[0]; // imerominia
                wra = part[2]; // ora
                LocalDateTime now = LocalDateTime.now();
                String[] date = dtf.format(now).split(" ");
                String date_now_str = date[0]; //imerominia twra
                if(imerominia.equalsIgnoreCase(date_now_str)){
                    simerinaRoute.add(imerominia+" "+wra);
                }
            }
            meraWraList.add(null);
        }
        return meraWraList;
    }
}