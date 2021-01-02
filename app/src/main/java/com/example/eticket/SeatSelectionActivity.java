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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eticket.Model.Route;
import com.example.eticket.Model.Seat;
import com.google.common.io.Resources;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatSelectionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        LinearLayout ll;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(195,130);
        params.topMargin = 15;
        List<Button> btnList = new ArrayList<>();
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
//                btn.setTextColor(Color.parseColor("#a20000")); //red
                btn.setLayoutParams(params);
                btn.setText(String.valueOf(counter));
                btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        CollectionReference db = FirebaseFirestore.getInstance().collection("Taken Seats");
                        Route route = (Route) getIntent().getSerializableExtra("route");
                        Log.d("route",route.toString());

                        List<String> myList = Arrays.asList(String.valueOf(btn.getId()),"true");
                        Map<String, Boolean> takenSeat = new HashMap<>();
                        takenSeat.put(String.valueOf(btn.getId()), true);
                        db.document(route.toString()).set(takenSeat, SetOptions.merge());
                    }
                });
                ll.addView(btn);
                btnList.add(btn);
            }
        }
    }
}