package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.format.DateFormat;

import com.example.eticket.Model.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RouteActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String sp1Selection;
    String sp2Selection;
    String routeKey;
    String currentDate;
    int selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);;
    CalendarView myCalendar;
    SearchableSpinner sp1;
    SearchableSpinner sp2;
    Spinner sp3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        List<String> pros = new ArrayList<>();
        List<String> wra = new ArrayList<>();
        pros.add(getString(R.string.pros));
        wra.add(getString(R.string.wra));
        getSupportActionBar().hide();
        ProgressDialog dialog = ProgressDialog.show(RouteActivity.this, "",
                "Loading", true);
        sp1 = findViewById(R.id.spinnerApo);
        sp1.setTitle(getString(R.string.apo));
        sp1.setPositiveButton("OK");

        sp2 = findViewById(R.id.spinnerPros);
        sp2.setTitle(getString(R.string.pros));
        sp2.setPositiveButton("OK");

        sp3 = findViewById(R.id.spinnerWra);
        Button search = findViewById(R.id.search);

        myCalendar = (CalendarView) findViewById(R.id.calendarView);
        //disable dates before today
        myCalendar.setMinDate(new Date().getTime());
        db.collection("Origin").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    list.add(0, getString(R.string.apo));
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    sp1.setAdapter(fillSpinner(list));
                    dialog.dismiss();
                    sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            sp1Selection = sp1.getSelectedItem().toString();
                            if (!sp1Selection.equals(getString(R.string.apo))) {
                                ProgressDialog dialog1 = ProgressDialog.show(RouteActivity.this, "",
                                        "Loading", true);
                                updateSecondSpinner(sp1Selection, sp2);
                                dialog1.dismiss();
                            } else {
                                //an den exw epileksei afetiria adiase ta dio spinner
                                sp2.setAdapter(fillSpinner(pros));
                                sp3.setAdapter(fillSpinner(wra));
                            }
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
                            if (!sp2Selection.equals(getString(R.string.pros))) {
                                ProgressDialog dialog2 = ProgressDialog.show(RouteActivity.this, "",
                                        "Loading", true);
                                routeKey = sp1Selection + "-" + sp2Selection;
                                getDatabaseHours(routeKey, sp3);
                                dialog2.dismiss();
                            }
                            else{
                                //an den dialeksw proorismo adeiase tis wres
                                sp3.setAdapter(fillSpinner(wra));
                            }
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

                dateListener();

                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentDate == "" || currentDate == null) {
                            currentDate = DateFormat.format("dd-MM-yyyy", myCalendar.getDate()).toString();
                        }

                        if (sp1Selection == getString(R.string.apo)) {
                            Toast.makeText(RouteActivity.this, getString(R.string.epilogiAfetirias), Toast.LENGTH_SHORT).show();
                        } else if (sp2Selection == getString(R.string.pros)) {
                            Toast.makeText(RouteActivity.this, getString(R.string.epilogiProorismou), Toast.LENGTH_SHORT).show();
                        } else if (sp3.getSelectedItem().toString() == getString(R.string.wra)) {
                            Toast.makeText(RouteActivity.this, getString(R.string.epilogiOras), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent signIn = new Intent(getBaseContext(), SeatSelectionActivity.class);
                            Route route = new Route(currentDate, routeKey, sp3.getSelectedItem().toString());

                            signIn.putExtra("route", route);
                            startActivity(signIn);
                        }
                    }
                });
            }
        });
    }


    private void dateListener() {
        myCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //date formatting
                String zeroDay = "";
                String zeroMonth = "";
                if (dayOfMonth / 10 < 1) {
                    zeroDay = "0";
                }
                if (month / 10 < 1) {
                    zeroMonth = "0";
                }
                currentDate = zeroDay + dayOfMonth + "-" + zeroMonth + (month + 1) + "-" + year;
                selectedDay = dayOfMonth;
                Log.d("Testing1","mpika sto calendar");
                getDatabaseHours(routeKey,sp3);
            }
        });
    }

    private void getDatabaseHours(String routeKey, Spinner sp3) {
        db.collection("Routes").document(routeKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> list = (List<String>) document.get("ΩΡΑ");
                        List<String> availableRoutesAfterNowTime = new ArrayList<>();
                        availableRoutesAfterNowTime.add(0, getString(R.string.wra));
                        for(int i=0;i<list.size();i++){
                            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                            //an currentDay==selectedDay (tou picker) checkare an i twrini wra einai prin apo tis wres twn routes
                            //an currentDay!=selectedDay den mas niazei i wra opote mpes
                            if(currentDay==selectedDay && LocalTime.now().isBefore(LocalTime.parse(list.get(i)))){
                                availableRoutesAfterNowTime.add(list.get(i));
                            }
                            else if(currentDay!=selectedDay){
                                availableRoutesAfterNowTime.add(list.get(i));
                            }
                        }
                        sp3.setAdapter(fillSpinner(availableRoutesAfterNowTime));
                        Log.d("LogTesting", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("LogTesting", "No such document");
                    }
                } else {
                    Log.d("LogTesting", "get failed with ", task.getException());
                }
            }
        });
    }

    private void updateSecondSpinner(String selection, Spinner sp) {
        DocumentReference docRef = db.collection("Origin").document(selection);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> list = (List<String>) document.get("Destination");
                        list.add(0, getString(R.string.pros));
                        sp.setAdapter(fillSpinner(list));
                        Log.d("LogTesting", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("LogTesting", "No such document");
                    }
                } else {
                    Log.d("LogTesting", "get failed with ", task.getException());
                }
            }
        });
    }

    private ArrayAdapter<String> fillSpinner(List<String> mList) {
        String[] array = new String[mList.size()];
        mList.toArray(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}


