package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.BuddhistCalendar;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eticket.Model.Route;
import com.example.eticket.Model.Seat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RouteActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String sp1Selection;
    String sp2Selection;
    String routeKey;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Spinner sp1 = findViewById(R.id.spinner1);
        List<String> li = new ArrayList<>();
        li.add("ΑΠΟ");
        sp1.setAdapter(fillSpinner(li));
        li.clear();

        Spinner sp2 = findViewById(R.id.spinner2);
        Spinner sp3 = findViewById(R.id.spinner3);
        Button search = findViewById(R.id.search);
        db.collection("Origin").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    list.add(0, "ΑΠΟ");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    sp1.setAdapter(fillSpinner(list));
                    sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            sp1Selection = sp1.getSelectedItem().toString();
                            updateSecondSpinner(sp1Selection,sp2);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            //Do nothing
                        }
                    });
                    sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            sp2Selection = sp2.getSelectedItem().toString();
                            routeKey = sp1Selection+"-"+sp2Selection;
                            getDatabaseHours(routeKey,sp3);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            //Do nothing
                        }
                    });

                    Log.d("LogTesting", list.toString());
                } else {
                    Log.d("LogTesting", "Error getting documents: ", task.getException());
                }
                search.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        CalendarView myCalendar = (CalendarView) findViewById(R.id.calendarView);

                        Intent signIn = new Intent(getBaseContext(), SeatSelectionActivity.class);
                        Route route = new Route(new Date(myCalendar.getDate()),routeKey,sp3.getSelectedItem().toString());
                        signIn.putExtra("route",route);
                        startActivity(signIn);
                    }
                });
            }
        });

        //"16/4 ΑΘΗΝΑ-ΑΜΑΛΙΑΔΑ 16:30"


//        myCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(year, month, dayOfMonth);
//                String date = year+"-"+month+"-"+dayOfMonth;
//                List<String> test = new ArrayList<>();
//                test.add(String.valueOf(date));
//            }
//        });
    }

    private void getDatabaseHours(String routeKey, Spinner sp3){
        db.collection("Routes").document(routeKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> list = (List<String>) document.get("ΩΡΑ");
                        sp3.setAdapter(fillSpinner(list));
                        Log.d("LogTesting", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("LogTesting", "No such document");
                    }
                } else {
                    Log.d("LogTesting", "get failed with ", task.getException());
                }
            }});
    }

    private void updateSecondSpinner(String selection, Spinner sp){
        DocumentReference docRef = db.collection("Origin").document(selection);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> list = (List<String>) document.get("Destination");
                        list.add(0, "ΠΡΟΣ");
                        sp.setAdapter(fillSpinner(list));
                        Log.d("LogTesting", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("LogTesting", "No such document");
                    }
                } else {
                    Log.d("LogTesting", "get failed with ", task.getException());
                }
            }});
    }

    private ArrayAdapter<String> fillSpinner(List<String> mList) {
            String[] cities = new String[mList.size()];
            mList.toArray(cities);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, cities);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            return adapter;
    }
}


