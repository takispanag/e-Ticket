package com.example.eticket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eticket.Model.Route;
import com.example.eticket.Model.Seat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.io.Resources;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatSelectionActivity extends AppCompatActivity {
    final CollectionReference db = FirebaseFirestore.getInstance().collection("Contested Seats");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        LinearLayout ll;

        Route route = (Route) getIntent().getSerializableExtra("route");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(195,130);
        params.topMargin = 15;
        List<Button> btnList = new ArrayList<>();
        List<Button> userSeats = new ArrayList<>();


        int counter=0;
        int busSize = 10; //get busSize from database, hardcoded for now
        for(int i=1;i<=busSize;i++){
            for(int j=1;j<=4;j++){
                ll = (LinearLayout) findViewById(getResources().getIdentifier("row"+j,"id" ,getPackageName()));
                Button btn = new Button(this);
                btn.setId(++counter);
                btn.setBackgroundResource(R.drawable.seat);
                btn.setTextSize(20);
                btn.setPadding(0,0,0,25);

                btn.setTextColor(Color.parseColor("#00b500")); //green
                btn.setLayoutParams(params);
                btn.setText(String.valueOf(counter));
                btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Log.d("route",route.toString());
                        //if(btn.getTextColors())
                        btn.setTextColor(Color.parseColor("#a20000"));
                        btn.setEnabled(false);
                        userSeats.add(btn);

                        Log.d("LogTesting",String.valueOf(userSeats.size()));
                        Map<String, String> takenSeat = new HashMap<>();
                        takenSeat.put(String.valueOf(btn.getId()), mAuth.getCurrentUser().getUid());
                        db.document(route.toString()).set(takenSeat, SetOptions.merge());
                    }
                });
                ll.addView(btn);
                btnList.add(btn);
                db.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                            List<Integer> contestedKeys = new ArrayList<>();
                                            for ( String key : dc.getDocument().getData().keySet() ) {
                                                contestedKeys.add(Integer.parseInt(key));
                                            }
                                            for(Button i : btnList){
                                                if(contestedKeys.contains(i.getId())){
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
        setKeyColor(route,btnList);
    }

    private void setKeyColor(Route route, List<Button> btnList){
        List<Integer> keyList = new ArrayList<>();
        db.document(route.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        for ( String key : document.getData().keySet() ) {
                            keyList.add(Integer.parseInt(key));
                        }
                        for(Button i : btnList){
                            if(keyList.contains(i.getId())){
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