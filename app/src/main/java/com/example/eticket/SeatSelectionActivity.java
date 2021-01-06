package com.example.eticket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eticket.Model.Route;
import com.google.android.gms.common.internal.FallbackServiceBroker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeatSelectionActivity extends AppCompatActivity {
    final CollectionReference dbReservedSeats = FirebaseFirestore.getInstance().collection("Reserved Seats");
    final CollectionReference dbUserSeats = FirebaseFirestore.getInstance().collection("UserSeats");
    Boolean firstRun = true;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    List<Button> userSeats = new ArrayList<>();
    List<Integer> seatsId = new ArrayList<>();
    List<Integer> oldSeatsId = new ArrayList<>();
    Map<String, List<Integer>> newUpdatedList = new HashMap<>();
    Map<String, List<Integer>> userRouteTakenSeats = new HashMap<>();
    Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        route = (Route) getIntent().getSerializableExtra("route");

        Button btnSearch = (Button) findViewById(R.id.kleiseThesi);

        LinearLayout ll;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(195, 130);
        params.topMargin = 15;
        List<Button> btnList = new ArrayList<>();


        int counter = 0;
        int busSize = 10; //get busSize from database, hardcoded for now
        for (int i = 1; i <= busSize; i++) {
            for (int j = 1; j <= 4; j++) {
                ll = (LinearLayout) findViewById(getResources().getIdentifier("row" + j, "id", getPackageName()));
                Button btn = new Button(this);
                btn.setId(++counter);
                btn.setBackgroundResource(R.drawable.seat);
                btn.setTextSize(20);
                btn.setPadding(0, 0, 0, 25);

                btn.setTextColor(Color.parseColor("#00b500")); //green
                btn.setLayoutParams(params);
                btn.setText(String.valueOf(counter));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("route", route.toString());
                        btn.setTextColor(Color.parseColor("#a20000"));
                        btn.setEnabled(false);
                        userSeats.add(btn);

                        Log.d("LogTesting", String.valueOf(userSeats.size()));
                        Map<String, String> takenSeat = new HashMap<>();
                        takenSeat.put(String.valueOf(btn.getId()), mAuth.getCurrentUser().getUid());
                        dbReservedSeats.document(route.toString()).set(takenSeat, SetOptions.merge());
                            dbUserSeats.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //if first run get old reserved seats list
                                            if (firstRun) {
                                                oldSeatsId = (List<Integer>) document.get(route.toString());
                                                firstRun = false;
                                            }
                                            //check if document has fields
                                            Map<String, Object> map = document.getData();
                                            if (map.size() == 0 || map.isEmpty()) {
                                                seatsId.add(btn.getId());
                                                userRouteTakenSeats.put(route.toString(), seatsId);
                                                dbUserSeats.document(mAuth.getCurrentUser().getUid()).set(userRouteTakenSeats, SetOptions.merge());
                                                Log.d("testing1", "Document is empty!");
                                            } else {
                                                seatsId = (List<Integer>) document.get(route.toString());
                                                seatsId.add(btn.getId());
                                                Log.d("testing1", Arrays.toString(seatsId.toArray()));
                                                userRouteTakenSeats.put(route.toString(), seatsId);
                                                dbUserSeats.document(mAuth.getCurrentUser().getUid()).set(userRouteTakenSeats, SetOptions.merge());
                                                Log.d("testing1", "Document is not empty!");
                                            }
                                        } else {
                                            Log.d("LogTesting", "No such document");
                                        }
                                    } else {
                                        Log.d("LogTesting", "get failed with ", task.getException());
                                    }
                                }
                            });

                    }
                });
                ll.addView(btn);
                btnList.add(btn);


                List<Integer> contestedKeys = new ArrayList<>();
                dbReservedSeats.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("LogTesting", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("LogTesting", "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    for (String key : dc.getDocument().getData().keySet()) {
                                        contestedKeys.add(Integer.parseInt(key));
                                    }
                                    for (Button i : btnList) {
                                        if (contestedKeys.contains(i.getId())) {
                                            i.setTextColor(Color.parseColor("#a20000")); //red
                                            i.setEnabled(false);
                                        }
                                    }
                                    Log.d("LogTesting", "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d("LogTesting", "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
            }
        }
        setKeyColor(route, btnList);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userSeats.isEmpty()) {
                    Toast.makeText(SeatSelectionActivity.this, "Please select one or more seats.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent kleiseThesi = new Intent(SeatSelectionActivity.this, PaymentActivity.class);
                    kleiseThesi.putExtra("size", userSeats.size());
                    startActivity(kleiseThesi);
                }
            }
        });
    }

    //When back button pressed delete selected seats, update database and userInfo seats
    @Override
    public void onBackPressed() {
        Map<String, Object> deleteField = new HashMap<>();
        Map<String, Object> delete2 = new HashMap<>();
        for (Button key : userSeats) {
            delete2.put(route.toString(), FieldValue.delete());
            deleteField.put(String.valueOf(key.getId()), FieldValue.delete());
        }
        dbReservedSeats.document(route.toString()).update(deleteField);
        dbUserSeats.document(mAuth.getCurrentUser().getUid()).update(delete2);

        //update database with old reserved seats
        if((oldSeatsId!=null)){
            if(!(oldSeatsId.isEmpty())){
                newUpdatedList.put(route.toString(), oldSeatsId);
                dbUserSeats.document(mAuth.getCurrentUser().getUid()).set(newUpdatedList);
            }
        }
        super.onBackPressed();
    }

    private void setKeyColor(Route route, List<Button> btnList) {
        List<Integer> keyList = new ArrayList<>();
        dbReservedSeats.document(route.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        for (String key : document.getData().keySet()) {
                            keyList.add(Integer.parseInt(key));
                        }
                        for (Button i : btnList) {
                            if (keyList.contains(i.getId())) {
                                i.setTextColor(Color.parseColor("#a20000")); //red
                                i.setEnabled(false);
                            }
                        }
                    } else {
                        keyList.add(0);
                        Log.d("LogTesting", "No such document");
                    }
                } else {
                    Log.d("LogTesting", "get failed with ", task.getException());
                }
            }
        });
    }
}