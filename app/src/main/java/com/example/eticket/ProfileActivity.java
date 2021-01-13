package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eticket.Model.CustomAdapter;
import com.example.eticket.Model.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ProfileActivity extends AppCompatActivity implements CustomAdapter.ItemClickListener{

    CustomAdapter adapter;
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    CollectionReference dbSeats = FirebaseFirestore.getInstance().collection("UserSeats");
    CollectionReference dbUserInfo = FirebaseFirestore.getInstance().collection("UserInfo");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<String> userSeats = new ArrayList<>();
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    StorageReference ref = storage.getReference();
    private String imagePath;
    private byte[] imgByteArray;
    private Bitmap imgBitmap;
    ImageView imageView;
    File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);getActionBar();
        //set action bar params
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#36363b")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "My Profile" + "</font>"));

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
                        TextView name = findViewById(R.id.name_text);
                        TextView email = findViewById(R.id.email_text);
                        name.setText("Όνομα:\n"+document.get("name"));
                        email.setText("Email:\n"+document.get("email"));
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
//                    String r = route.toString();
                    if (document.exists()) {
                        Log.d("takis", "DocumentSnapshot data: " + document.getData());
                        for ( Map.Entry<String, Object> entry : document.getData().entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue().toString();
                            userSeats.add(key+", Θέση: "+value.toString());
                            // do something with key and/or tab
                        }
                    } else {
                        Log.d("theseis ", "No such document");
                    }
                } else {
                    Log.d("theseis ", "get failed with ", task.getException());
                }

                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.recycleView);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                adapter = new CustomAdapter(ProfileActivity.this, userSeats);
                adapter.setClickListener(ProfileActivity.this);
                recyclerView.addItemDecoration(new DividerItemDecoration(ProfileActivity.this,
                        DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(adapter);
            }
        });

        imageView.setOnClickListener((View view) -> selectPhoto());
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
                    imgBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
        image.setImageBitmap(QRCode.from(mAuth.getCurrentUser().getUid()).bitmap());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Το QR code του εισιτηρίου σας.");
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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.AboutUs:
                //your action
                Intent about_us = new Intent(ProfileActivity.this,AboutUsActivity.class);
                startActivity(about_us);
                break;
            case R.id.kleise_thesi:
                //your action
                Intent kleise_thesi = new Intent(ProfileActivity.this,RouteActivity.class);
                startActivity(kleise_thesi);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}