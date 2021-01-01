package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.List;

public class RouteActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Spinner sp1 = findViewById(R.id.spinner1);
        Spinner sp2 = findViewById(R.id.spinner2);
        Spinner sp3 = findViewById(R.id.spinner3);
        db.collection("Origin").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    list.add(0, "ΑΠΟ");
                    sp1.setAdapter(fillSpinner(list));
                    sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String sp1Selection = sp1.getSelectedItem().toString();
                            updateSecondSpinner(sp1Selection,sp2);
                            String sp2Selection = sp2.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            //Do nothing
                        }

                    });
                    Log.d("TAG", list.toString());
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
        CalendarView myCalendar = (CalendarView) findViewById(R.id.calendarView);

        myCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                List<String> test = new ArrayList<>();
                test.add(String.valueOf(day));
                sp3.setAdapter(fillSpinner(test));
            }
        });
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
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }});
    }

    private ArrayAdapter<String> fillSpinner(List<String> mList) {
            String[] group = new String[mList.size()];
            mList.toArray(group);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, group);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Log.d("TAG", "DocumentSnapshot data: " + group);
            return adapter;
    }
}


