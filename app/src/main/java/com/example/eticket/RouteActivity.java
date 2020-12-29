package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RouteActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Spinner sp1 = findViewById(R.id.spinner1);
        Spinner sp2 = findViewById(R.id.spinner2);
        CollectionReference docRef = db.collection("Origin");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    List<String> list = new
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        sp1.setAdapter(fillSpinner(document));
//                        sp2.setAdapter(fillSpinner(document));
//                    } else {
//                        Log.d("TAG", "No such document");
//                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private ArrayAdapter<String> fillSpinner(DocumentSnapshot document){
        List<String> list = (List<String>) document.get("Destination");
        list.add(0,"Προς");
        String[] group = new String[list.size()];
        list.toArray(group);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, group);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Log.d("TAG", "DocumentSnapshot data: " + group);
        return adapter;
    }



//        List<String> arr = Arrays.asList(
//                "ΑΜΑΛΙΑΔΑ",
//                "ΑΝΔΡΑΒΙΔΑ",
//                "ΑΡΧΑΙΑ ΟΛΥΜΠΙΑ",
//                "ΒΑΡΔΑ",
//                "ΓΑΣΤΟΥΝΗ",
//                "ΓΙΑΝΝΙΤΣΟΧΩΡΙ",
//                "ΚΡΕΣΤΕΝΑ",
//                "ΖΑΧΑΡΩ",
//                "ΚΑΒΑΣΙΛΑ" ,
//                "ΛΕΧΑΙΝΑ",
//                "ΝΕΑ ΜΑΝΩΛΑΔΑ",
//                "ΠΥΡΓΟΣ",
//                "ΣΑΒΒΑΛΙΑ");
//        Map<String, List<String>> city = new HashMap<>();
//        city.put("Destination", arr);
//
//        db.collection("Origin").document("Athina")
//                .set(city)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("test", "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("test", "Error writing document", e);
//                    }
//                });
}
