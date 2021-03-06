package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eticket.Model.CustomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProfileActivity extends AppCompatActivity implements CustomAdapter.ItemClickListener{

    CustomAdapter adapter;
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    CollectionReference dbSeats = FirebaseFirestore.getInstance().collection("UserSeats");
    CollectionReference dbUserInfo = FirebaseFirestore.getInstance().collection("UserInfo");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    List<String> myUserRoutes = new ArrayList<>();
    List<String> simerinaRoute = new ArrayList<>();
    Boolean first_Notification = true;
    List<String> userRoutes = new ArrayList<>();
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    StorageReference ref = storage.getReference();
    private String imagePath;
    private byte[] imgByteArray;
    private Bitmap imgBitmap;
    ImageView imageView;
    File localFile;
    String name,email;
    Button confirm_email;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getActionBar();
        //set action bar params

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#36363b")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "My Profile" + "</font>"));

        //otan patisw allagi stoixeiwn pigene sto activity editCredentials
        Button allagi_stoixeiwn = findViewById(R.id.EditCredentials);
        allagi_stoixeiwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent allagi_stoixeiwn = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(allagi_stoixeiwn);
            }
        });

        //otan patisw kleise thesi pigene sto activity klise thesi
        Button kleise_thesi = findViewById(R.id.kleise_thesi_profile);
        kleise_thesi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kleise_thesi = new Intent(ProfileActivity.this, RouteActivity.class);
                startActivity(kleise_thesi);
            }
        });

        //button confirm email na svistei an einai verified
        try{
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                Button btn =(Button)findViewById(R.id.confirm_email);
                btn.setVisibility(View.GONE);
            }
            else{
                //stile email confirmation notification
                if(getIntent().getExtras() == null || (getIntent().getExtras()!=null && getIntent().getExtras().getBoolean("first_Notification"))){
                    sendNotification(Collections.emptyList(),0,"ConfirmEmail");
                }

                Executors.newSingleThreadExecutor().execute(() -> {
                    while (true){
                        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                            break;
                        }
                        if(isUserVerified()){
                            runOnUiThread(() -> Toast.makeText(ProfileActivity.this, getString(R.string.emailConfirmed), Toast.LENGTH_SHORT).show());
                            //intent ston eayto moy
                            finish();
                            startActivity(getIntent().putExtra("first_Notification",first_Notification));
                            break;
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }catch (Exception e){
            Log.e("pipinho",e.toString());
        }

        //otan patithei to koympi confirm email stile email confirmation
        confirm_email = (Button) findViewById(R.id.confirm_email);
        confirm_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if not verified
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                FirebaseAuth.getInstance().getCurrentUser().reload();
                Toast.makeText(ProfileActivity.this, getString(R.string.confirmationEmailSent), Toast.LENGTH_SHORT).show();
            }
        });

        dbSeats.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        for ( String key : document.getData().keySet() ) {
                            myUserRoutes.add(key);
                        }
                        //notification 30 lepta prin tin anaxwrisi
                        split("",myUserRoutes,"SimerinaDromologia");//gemizw simerina route list

                        //            //paradeigma
                        //            String s = "14-01-2021 ΑΜΑΛΙΑΔΑ-ΑΚΡΑΤΑ 21:31";
                        //
                        //            String wra = "";
                        //            String imerominia = "";
                        //            String[] part = s.split(" "); //returns an array with the 3 parts
                        //            imerominia = part[0]; // imerominia
                        //            wra = part[2]; // ora

                        //notification if 30 minutes before departure
                        Executors.newSingleThreadExecutor().execute(() -> {
                            while (true){
                                for(int i = 0;i<simerinaRoute.size();i++){
                                    List<String> dromologioMeraWra = split(simerinaRoute.get(i), Collections.emptyList(),"Dromologio");
                                    List<String> twriniMeraWra = split("",Collections.emptyList(),"TwriniWra");


                                    SimpleDateFormat formatWra = new SimpleDateFormat("HH:mm");
                                    Date time_now = null;
                                    Date wra_anaxwrisis = null;
                                    try {
                                        time_now = formatWra.parse(twriniMeraWra.get(1));
                                        wra_anaxwrisis = formatWra.parse(dromologioMeraWra.get(2));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long diff = wra_anaxwrisis.getTime() - time_now.getTime();
                                    int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(diff);

                                    if(dromologioMeraWra.get(0).equalsIgnoreCase(twriniMeraWra.get(0)) && minutes<=30 && minutes>=0 && first_Notification) {
                                        sendNotification(dromologioMeraWra,minutes,"Dromologio");
                                    }
                                }
                                try {
                                    Thread.sleep(60000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }});
                    } else {
                        Log.d("theseis ", "No such document");
                    }
                } else {
                    Log.d("theseis ", "get failed with ", task.getException());
                }
            }
        });

        imageView = findViewById(R.id.imageViewProfile);
        ProgressDialog dialog = ProgressDialog.show(ProfileActivity.this, "",
                "Loading", true);

        StorageReference profile_img = ref.child(mAuth.getCurrentUser().getUid()+"/profile_picture");
        //get profile picture if it exists in database
        localFile = null;
        try {
            localFile = File.createTempFile("profile_picture", "jpg");
            profile_img.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    imageView =(ImageView) findViewById(R.id.imageViewProfile);
                    Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    dialog.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set onoma kai email
        dbUserInfo.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        TextView name_textView = findViewById(R.id.name_text);
                        TextView email_textView = findViewById(R.id.email_text);
                        name = document.get("name").toString();
                        email = document.get("email").toString();
                        name_textView.setText("Όνομα:\n"+name);
                        email_textView.setText("Email:\n"+email);
                    } else {
                        Log.d("theseis ", "No such document");
                    }
                } else {
                    Log.d("theseis ", "get failed with ", task.getException());
                }
            }
        });

        // data to populate the RecyclerView with
        dbSeats.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("takis", "DocumentSnapshot data: " + document.getData());
                        for ( Map.Entry<String, Object> entry : document.getData().entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue().toString();
                            userRoutes.add(key+", Θέση: "+value.toString());
                        }
                        if(userRoutes.size()==0){
                            userRoutes.add(getString(R.string.kanenaDromologio));
                        }
                        Collections.sort(userRoutes);
                    } else {
                        Log.d("theseis ", "No such document");
                    }
                } else {
                    Log.d("theseis ", "get failed with ", task.getException());
                }

                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.recycleView);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                adapter = new CustomAdapter(ProfileActivity.this, userRoutes);
                adapter.setClickListener(ProfileActivity.this);
                recyclerView.addItemDecoration(new DividerItemDecoration(ProfileActivity.this,
                        DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(adapter);
            }
        });
        imageView.setOnClickListener((View view) -> selectPhoto());
    }

    private boolean isUserVerified() {
        try{
            FirebaseAuth.getInstance().getCurrentUser().reload();
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                return true;
            }
            else{
                return false;
            }
        }catch(Exception e){
            Log.e("pipinho",e.toString());
            return false;
        }
    }


    //notification 30 lepta prin tin anaxwrisi i otan o xristis den exei kanei confirm to email tou
    private void sendNotification(List<String> dromologioMeraWra, int minutes, String type) {
        first_Notification = false;
        int NOTIFICATION_ID = 0;
        int NOTIFICATION_ID_ROUTE = 234;
        int NOTIFICATION_ID_EMAIL_CONFIRM = 250;
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
        NotificationCompat.Builder builder = null;

        if(type.equals("Dromologio")){
            builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                    .setContentTitle(getString(R.string.ipenthimisi))
                    .setContentText(getString(R.string.toDromologio) +dromologioMeraWra.get(1)+getString(R.string.xekinaei)+minutes+getString(R.string.metavasiStoXoro))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.toDromologio) +dromologioMeraWra.get(1)+getString(R.string.xekinaei)+minutes+getString(R.string.metavasiStoXoro)))
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setLargeIcon( BitmapFactory.decodeResource(getBaseContext().getResources(),
                            R.drawable.bus));
            NOTIFICATION_ID = NOTIFICATION_ID_ROUTE;
        }
        else if (type.equals("ConfirmEmail")){
            builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                    .setContentTitle(getString(R.string.ipenthimisi))
                    .setContentText(getString(R.string.emailDenExeiEpiveveothei))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.emailDenExeiEpiveveothei)))
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setLargeIcon( BitmapFactory.decodeResource(getBaseContext().getResources(),
                            R.drawable.ic_email));
            NOTIFICATION_ID = NOTIFICATION_ID_EMAIL_CONFIRM;
        }

        Intent resultIntent = new Intent(getBaseContext(), ProfileActivity.class);
        resultIntent.putExtra("first_Notification",first_Notification);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    //epistrefei list aapo string me tin mera,dromologio,wra i gemizei ta simerina routes
    public List<String> split(String myRoute, List<String> myUserRoutes, String type){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        List<String> meraWraList = new ArrayList<>();
        if(type.equals("Dromologio")){
            String wra = "", imerominia = "", dromologio = "";
            Matcher matcher = Pattern.compile("(\\d{2}-\\d{2}-\\d{4}) ([Α-Ω]+ ?[Α-Ω]+-[Α-Ω]+ ?[Α-Ω]+ )?(\\d{2}:\\d{2})").matcher(myRoute);
            if(matcher.matches()){
                imerominia = matcher.group(1); // imerominia
                dromologio = matcher.group(2); //dromologio
                wra = matcher.group(3); // ora
            }
            meraWraList.add(imerominia);
            meraWraList.add(dromologio);
            meraWraList.add(wra);
        }
        else if(type.equals("TwriniWra")){
            LocalDateTime now = LocalDateTime.now();
            String[] date = dtf.format(now).split(" ");
            String imerominia = date[0]; //imerominia twra
            String wra = date[1]; //wra twra
            meraWraList.add(imerominia);
            meraWraList.add(wra);
        }
        else if(type.equals("SimerinaDromologia")) {
            for (int i = 0; i < myUserRoutes.size(); i++) {
                String wra = "", imerominia = "", dromologio = "";
                String myRouteLocal = myUserRoutes.get(i);
                Matcher matcher = Pattern.compile("(\\d{2}-\\d{2}-\\d{4}) ([Α-Ω]+ ?[Α-Ω]+-[Α-Ω]+ ?[Α-Ω]+)? (\\d{2}:\\d{2})").matcher(myRouteLocal);
                if(matcher.matches()){
                    imerominia = matcher.group(1); // imerominia
                    dromologio = matcher.group(2); //dromologio
                    wra = matcher.group(3); // ora
                }
                LocalDateTime now = LocalDateTime.now();
                String[] date = dtf.format(now).split(" ");
                String date_now_str = date[0]; //imerominia twra
                if (imerominia.equalsIgnoreCase(date_now_str)) {
                    simerinaRoute.add(imerominia + " " +dromologio+" "+ wra);
                }
            }
        }
        return meraWraList;
    }

    //kane update tin fwtografia sto app kai upload stin vasi
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

            try {
                imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Executors.newSingleThreadExecutor().execute(() -> {
                    imgBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                    imgByteArray = bos.toByteArray();

                });
            } catch (Exception e) {
                System.out.println(e);
            }
            imageView.setVisibility(View.VISIBLE);
        }

        StorageReference img =	ref.child(mAuth.getCurrentUser().getUid()+"/"+"profile_picture");
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteData = baos.toByteArray();

        UploadTask uploadTask = img.putBytes(byteData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(ProfileActivity.this, "Επιτυχής αλλαγή φωτογραφίας.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //epilogi fotografias apo kinito
    private void selectPhoto() {
        Intent gallery =
                new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    //click event gia to recycler view
    @Override
    public void onItemClick(View view, int position) {
        //alert box me to qr tou xristi prosorina apo to UID
        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image.setAdjustViewBounds(true);
        ViewGroup.LayoutParams params = image.getLayoutParams();
        if (params != null) {
            params.width= 150;
            params.height = 150;
        } else{
            params = new ViewGroup.LayoutParams(150, 150);
        }

        image.setLayoutParams(params);
        image.setImageBitmap(QRCode.from(userRoutes.get(position)).bitmap()); //qr eisitiriwn

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.qrCode));
        builder.setView(image);
        builder.setPositiveButton("Ok", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.aboutUs:
                Intent about_us = new Intent(ProfileActivity.this,AboutUsActivity.class);
                startActivity(about_us);
                break;
            case R.id.aposindesi:
                Intent aposindesi = new Intent(ProfileActivity.this,MainActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(aposindesi);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}