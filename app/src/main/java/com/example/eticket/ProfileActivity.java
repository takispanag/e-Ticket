package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eticket.Model.CustomAdapter;
import com.example.eticket.Model.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity implements CustomAdapter.ItemClickListener{

    CustomAdapter adapter;
    CollectionReference dbSeats = FirebaseFirestore.getInstance().collection("UserSeats");
    CollectionReference dbUserInfo = FirebaseFirestore.getInstance().collection("UserInfo");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<String> userSeats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);getActionBar();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#36363b")));
        setTitle("My profile");
        setTitle(Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.app_name) + "</font>"));

        ProgressDialog dialog = ProgressDialog.show(ProfileActivity.this, "",
                "Loading", true);
        dbUserInfo.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
//                    String r = route.toString();
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
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.fingerprint:
                //your action
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