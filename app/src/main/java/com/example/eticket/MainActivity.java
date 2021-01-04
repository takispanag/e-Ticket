package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eticket.Model.Route;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn,btnSignUp,btnButton,btnButton2;
    TextView txtSlogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnButton = (Button) findViewById(R.id.button);

        txtSlogan = (TextView) findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);


//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        List<String> routeList = Arrays.asList(
//                "ΑΘΗΝΑ-ΑΜΑΛΙΑΔΑ",
//                "ΑΘΗΝΑ-ΑΝΔΡΑΒΙΔΑ",
//                "ΑΘΗΝΑ-ΑΡΧΑΙΑ ΟΛΥΜΠΙΑ",
//                "ΑΘΗΝΑ-ΒΑΡΔΑ",
//                "ΑΘΗΝΑ-ΓΑΣΤΟΥΝΗ",
//                "ΑΘΗΝΑ-ΓΙΑΝΝΙΤΣΟΧΩΡΙ",
//                "ΑΘΗΝΑ-ΚΡΕΣΤΕΝΑ",
//                "ΑΘΗΝΑ-ΖΑΧΑΡΩ",
//                "ΑΘΗΝΑ-ΚΑΒΑΣΙΛΑ",
//                "ΑΘΗΝΑ-ΛΕΧΑΙΝΑ",
//                "ΑΘΗΝΑ-ΝΕΑ ΜΑΝΩΛΑΔΑ",
//                "ΑΘΗΝΑ-ΠΥΡΓΟΣ",
//                "ΑΘΗΝΑ-ΣΑΒΒΑΛΙΑ",
//                "ΑΜΑΛΙΑΔΑ-ΑΓΧΙΑΛΟΣ",
//                "ΑΜΑΛΙΑΔΑ-ΑΘΗΝΑ",
//                "ΑΜΑΛΙΑΔΑ-ΑΙΓΙΟ",
//                "ΑΜΑΛΙΑΔΑ-ΑΚΡΑΤΑ",
//                "ΑΜΑΛΙΑΔΑ-ΑΜΦΙΣΣΑ",
//                "ΑΜΑΛΙΑΔΑ-ΑΝΤΙΡΙΟ",
//                "ΑΜΑΛΙΑΔΑ-ΓΑΛΑΞΙΔΙ",
//                "ΑΜΑΛΙΑΔΑ-ΓΡΑΒΙΑ",
//                "ΑΜΑΛΙΑΔΑ-ΒΕΡΟΙΑ",
//                "ΑΜΑΛΙΑΔΑ-ΒΟΛΟΣ",
//                "ΑΜΑΛΙΑΔΑ-ΚΑΤΕΡΙΝΗ",
//                "ΑΜΑΛΙΑΔΑ-ΛΙΤΟΧΩΡΟ",
//                "ΑΜΑΛΙΑΔΑ-ΣΟΥΡΠΗ",
//                "ΑΜΑΛΙΑΔΑ-ΕΛΕΥΣΙΝΑ",
//                "ΑΜΑΛΙΑΔΑ-ΘΕΣΣΑΛΟΝΙΚΗ",
//                "ΑΜΑΛΙΑΔΑ-ΙΣΘΜΟΣ",
//                "ΑΜΑΛΙΑΔΑ-ΙΤΕΑ",
//                "ΑΜΑΛΙΑΔΑ-ΚΙΑΤΟ",
//                "ΑΜΑΛΙΑΔΑ-ΛΑΜΙΑ",
//                "ΑΜΑΛΙΑΔΑ-ΛΑΡΙΣΣΑ",
//                "ΑΜΑΛΙΑΔΑ-ΛΕΥΚΟΚΑΡΥΑ",
//                "ΑΜΑΛΙΑΔΑ-ΜΑΚΡΥΓΙΑΛΟΣ",
//                "ΑΜΑΛΙΑΔΑ-ΜΕΓΑΡΑ",
//                "ΑΜΑΛΙΑΔΑ-ΜΠΡΑΛΟΣ",
//                "ΑΜΑΛΙΑΔΑ-ΝΑΥΠΑΚΤΟΣ",
//                "ΑΜΑΛΙΑΔΑ-ΠΛΑΤΑΜΩΝΑΣ",
//                "ΑΜΑΛΙΑΔΑ-ΣΤΥΛΙΔΑ",
//                "ΑΜΑΛΙΑΔΑ-ΤΕΜΠΗ",
//                "ΑΝΔΡΑΒΙΔΑ-ΑΓΧΙΑΛΟΣ",
//                "ΑΝΔΡΑΒΙΔΑ-ΑΘΗΝΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΑΙΓΙΟ",
//                "ΑΝΔΡΑΒΙΔΑ-ΑΚΡΑΤΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΑΜΦΙΣΣΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΑΝΤΙΡΙΟ",
//                "ΑΝΔΡΑΒΙΔΑ-ΓΑΛΑΞΙΔΙ",
//                "ΑΝΔΡΑΒΙΔΑ-ΓΡΑΒΙΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΒΕΡΟΙΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΒΟΛΟΣ",
//                "ΑΝΔΡΑΒΙΔΑ-ΚΑΤΕΡΙΝΗ",
//                "ΑΝΔΡΑΒΙΔΑ-ΛΙΤΟΧΩΡΟ",
//                "ΑΝΔΡΑΒΙΔΑ-ΣΟΥΡΠΗ",
//                "ΑΝΔΡΑΒΙΔΑ-ΕΛΕΥΣΙΝΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΘΕΣΣΑΛΟΝΙΚΗ",
//                "ΑΝΔΡΑΒΙΔΑ-ΙΣΘΜΟΣ",
//                "ΑΝΔΡΑΒΙΔΑ-ΙΤΕΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΚΙΑΤΟ",
//                "ΑΝΔΡΑΒΙΔΑ-ΛΑΜΙΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΛΑΡΙΣΣΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΛΕΥΚΟΚΑΡΥΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΜΑΚΡΥΓΙΑΛΟΣ",
//                "ΑΝΔΡΑΒΙΔΑ-ΜΕΓΑΡΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΜΠΡΑΛΟΣ",
//                "ΑΝΔΡΑΒΙΔΑ-ΝΑΥΠΑΚΤΟΣ",
//                "ΑΝΔΡΑΒΙΔΑ-ΠΛΑΤΑΜΩΝΑΣ",
//                "ΑΝΔΡΑΒΙΔΑ-ΣΤΥΛΙΔΑ",
//                "ΑΝΔΡΑΒΙΔΑ-ΤΕΜΠΗ",
//                "ΒΑΡΔΑ-ΑΓΧΙΑΛΟΣ",
//                "ΒΑΡΔΑ-ΑΘΗΝΑ",
//                "ΒΑΡΔΑ-ΑΙΓΙΟ",
//                "ΒΑΡΔΑ-ΑΚΡΑΤΑ",
//                "ΒΑΡΔΑ-ΑΜΦΙΣΣΑ",
//                "ΒΑΡΔΑ-ΑΝΤΙΡΙΟ",
//                "ΒΑΡΔΑ-ΓΑΛΑΞΙΔΙ",
//                "ΒΑΡΔΑ-ΓΡΑΒΙΑ",
//                "ΒΑΡΔΑ-ΒΕΡΟΙΑ",
//                "ΒΑΡΔΑ-ΒΟΛΟΣ",
//                "ΒΑΡΔΑ-ΚΑΤΕΡΙΝΗ",
//                "ΒΑΡΔΑ-ΛΙΤΟΧΩΡΟ",
//                "ΒΑΡΔΑ-ΣΟΥΡΠΗ",
//                "ΒΑΡΔΑ-ΕΛΕΥΣΙΝΑ",
//                "ΒΑΡΔΑ-ΘΕΣΣΑΛΟΝΙΚΗ",
//                "ΒΑΡΔΑ-ΙΣΘΜΟΣ",
//                "ΒΑΡΔΑ-ΙΤΕΑ",
//                "ΒΑΡΔΑ-ΚΙΑΤΟ",
//                "ΒΑΡΔΑ-ΛΑΜΙΑ",
//                "ΒΑΡΔΑ-ΛΑΡΙΣΣΑ",
//                "ΒΑΡΔΑ-ΛΕΥΚΟΚΑΡΥΑ",
//                "ΒΑΡΔΑ-ΜΑΚΡΥΓΙΑΛΟΣ",
//                "ΒΑΡΔΑ-ΜΕΓΑΡΑ",
//                "ΒΑΡΔΑ-ΜΠΡΑΛΟΣ",
//                "ΒΑΡΔΑ-ΝΑΥΠΑΚΤΟΣ",
//                "ΒΑΡΔΑ-ΠΛΑΤΑΜΩΝΑΣ",
//                "ΒΑΡΔΑ-ΣΤΥΛΙΔΑ",
//                "ΒΑΡΔΑ-ΤΕΜΠΗ",
//                "ΓΑΣΤΟΥΝΗ-ΑΓΧΙΑΛΟΣ",
//                "ΓΑΣΤΟΥΝΗ-ΑΘΗΝΑ",
//                "ΓΑΣΤΟΥΝΗ-ΑΙΓΙΟ",
//                "ΓΑΣΤΟΥΝΗ-ΑΚΡΑΤΑ",
//                "ΓΑΣΤΟΥΝΗ-ΑΜΦΙΣΣΑ",
//                "ΓΑΣΤΟΥΝΗ-ΑΝΤΙΡΙΟ",
//                "ΓΑΣΤΟΥΝΗ-ΓΑΛΑΞΙΔΙ",
//                "ΓΑΣΤΟΥΝΗ-ΓΡΑΒΙΑ",
//                "ΓΑΣΤΟΥΝΗ-ΒΕΡΟΙΑ",
//                "ΓΑΣΤΟΥΝΗ-ΒΟΛΟΣ",
//                "ΓΑΣΤΟΥΝΗ-ΚΑΤΕΡΙΝΗ",
//                "ΓΑΣΤΟΥΝΗ-ΛΙΤΟΧΩΡΟ",
//                "ΓΑΣΤΟΥΝΗ-ΣΟΥΡΠΗ",
//                "ΓΑΣΤΟΥΝΗ-ΕΛΕΥΣΙΝΑ",
//                "ΓΑΣΤΟΥΝΗ-ΘΕΣΣΑΛΟΝΙΚΗ",
//                "ΓΑΣΤΟΥΝΗ-ΙΣΘΜΟΣ",
//                "ΓΑΣΤΟΥΝΗ-ΙΤΕΑ",
//                "ΓΑΣΤΟΥΝΗ-ΚΙΑΤΟ",
//                "ΓΑΣΤΟΥΝΗ-ΛΑΜΙΑ",
//                "ΓΑΣΤΟΥΝΗ-ΛΑΡΙΣΣΑ",
//                "ΓΑΣΤΟΥΝΗ-ΛΕΥΚΟΚΑΡΥΑ",
//                "ΓΑΣΤΟΥΝΗ-ΜΑΚΡΥΓΙΑΛΟΣ",
//                "ΓΑΣΤΟΥΝΗ-ΜΕΓΑΡΑ",
//                "ΓΑΣΤΟΥΝΗ-ΜΠΡΑΛΟΣ",
//                "ΓΑΣΤΟΥΝΗ-ΝΑΥΠΑΚΤΟΣ",
//                "ΓΑΣΤΟΥΝΗ-ΠΛΑΤΑΜΩΝΑΣ",
//                "ΓΑΣΤΟΥΝΗ-ΣΤΥΛΙΔΑ",
//                "ΓΑΣΤΟΥΝΗ-ΤΕΜΠΗ",
//                "ΖΑΧΑΡΩ-ΑΓΧΙΑΛΟΣ",
//                "ΖΑΧΑΡΩ-ΑΘΗΝΑ",
//                "ΖΑΧΑΡΩ-ΑΙΓΙΟ",
//                "ΖΑΧΑΡΩ-ΑΚΡΑΤΑ",
//                "ΖΑΧΑΡΩ-ΑΜΦΙΣΣΑ",
//                "ΖΑΧΑΡΩ-ΑΝΤΙΡΙΟ",
//                "ΖΑΧΑΡΩ-ΓΑΛΑΞΙΔΙ",
//                "ΖΑΧΑΡΩ-ΓΡΑΒΙΑ",
//                "ΖΑΧΑΡΩ-ΒΕΡΟΙΑ",
//                "ΖΑΧΑΡΩ-ΒΟΛΟΣ",
//                "ΖΑΧΑΡΩ-ΚΑΤΕΡΙΝΗ",
//                "ΖΑΧΑΡΩ-ΛΙΤΟΧΩΡΟ",
//                "ΖΑΧΑΡΩ-ΣΟΥΡΠΗ",
//                "ΖΑΧΑΡΩ-ΕΛΕΥΣΙΝΑ",
//                "ΖΑΧΑΡΩ-ΘΕΣΣΑΛΟΝΙΚΗ",
//                "ΖΑΧΑΡΩ-ΙΣΘΜΟΣ",
//                "ΖΑΧΑΡΩ-ΙΤΕΑ",
//                "ΖΑΧΑΡΩ-ΚΙΑΤΟ",
//                "ΖΑΧΑΡΩ-ΛΑΜΙΑ",
//                "ΖΑΧΑΡΩ-ΛΑΡΙΣΣΑ",
//                "ΖΑΧΑΡΩ-ΛΕΥΚΟΚΑΡΥΑ",
//                "ΖΑΧΑΡΩ-ΜΑΚΡΥΓΙΑΛΟΣ",
//                "ΖΑΧΑΡΩ-ΜΕΓΑΡΑ",
//                "ΖΑΧΑΡΩ-ΜΠΡΑΛΟΣ",
//                "ΖΑΧΑΡΩ-ΝΑΥΠΑΚΤΟΣ",
//                "ΖΑΧΑΡΩ-ΠΛΑΤΑΜΩΝΑΣ",
//                "ΖΑΧΑΡΩ-ΣΤΥΛΙΔΑ",
//                "ΖΑΧΑΡΩ-ΤΕΜΠΗ",
//                "ΘΕΣΣΑΛΟΝΙΚΗ-ΑΜΑΛΙΑΔΑ",
//                "ΘΕΣΣΑΛΟΝΙΚΗ-ΑΝΔΡΑΒΙΔΑ",
//                "ΘΕΣΣΑΛΟΝΙΚΗ-ΒΑΡΔΑ",
//                "ΘΕΣΣΑΛΟΝΙΚΗ-ΓΑΣΤΟΥΝΗ",
//                "ΘΕΣΣΑΛΟΝΙΚΗ-ΛΕΧΑΙΝΑ",
//                "ΘΕΣΣΑΛΟΝΙΚΗ-ΝΕΑ ΜΑΝΩΛΑΔΑ",
//                "ΘΕΣΣΑΛΟΝΙΚΗ-ΠΥΡΓΟΣ",
//                "ΘΕΣΣΑΛΟΝΙΚΗ-ΣΑΒΒΑΛΙΑ",
//                "ΚΡΕΣΤΕΝΑ-ΑΘΗΝΑ",
//                "ΚΡΕΣΤΕΝΑ-ΑΓΧΙΑΛΟΣ",
//                "ΚΡΕΣΤΕΝΑ-ΑΙΓΙΟ",
//                "ΚΡΕΣΤΕΝΑ-ΑΚΡΑΤΑ",
//                "ΚΡΕΣΤΕΝΑ-ΑΜΦΙΣΣΑ",
//                "ΚΡΕΣΤΕΝΑ-ΑΝΤΙΡΙΟ",
//                "ΚΡΕΣΤΕΝΑ-ΓΑΛΑΞΙΔΙ",
//                "ΚΡΕΣΤΕΝΑ-ΓΡΑΒΙΑ",
//                "ΚΡΕΣΤΕΝΑ-ΒΕΡΟΙΑ",
//                "ΚΡΕΣΤΕΝΑ-ΒΟΛΟΣ",
//                "ΚΡΕΣΤΕΝΑ-ΚΑΤΕΡΙΝΗ",
//                "ΚΡΕΣΤΕΝΑ-ΛΙΤΟΧΩΡΟ",
//                "ΚΡΕΣΤΕΝΑ-ΣΟΥΡΠΗ",
//                "ΚΡΕΣΤΕΝΑ-ΕΛΕΥΣΙΝΑ",
//                "ΚΡΕΣΤΕΝΑ-ΘΕΣΣΑΛΟΝΙΚΗ",
//                "ΚΡΕΣΤΕΝΑ-ΙΣΘΜΟΣ",
//                "ΚΡΕΣΤΕΝΑ-ΙΤΕΑ",
//                "ΚΡΕΣΤΕΝΑ-ΚΙΑΤΟ",
//                "ΚΡΕΣΤΕΝΑ-ΛΑΜΙΑ",
//                "ΚΡΕΣΤΕΝΑ-ΛΑΡΙΣΣΑ",
//                "ΚΡΕΣΤΕΝΑ-ΛΕΥΚΟΚΑΡΥΑ",
//                "ΚΡΕΣΤΕΝΑ-ΜΑΚΡΥΓΙΑΛΟΣ",
//                "ΚΡΕΣΤΕΝΑ-ΜΕΓΑΡΑ",
//                "ΚΡΕΣΤΕΝΑ-ΜΠΡΑΛΟΣ",
//                "ΚΡΕΣΤΕΝΑ-ΝΑΥΠΑΚΤΟΣ",
//                "ΚΡΕΣΤΕΝΑ-ΠΛΑΤΑΜΩΝΑΣ",
//                "ΚΡΕΣΤΕΝΑ-ΣΤΥΛΙΔΑ",
//                "ΚΡΕΣΤΕΝΑ-ΤΕΜΠΗ",
//                "ΛΕΧΑΙΝΑ-ΑΘΗΝΑ",
//                "ΛΕΧΑΙΝΑ-ΑΓΧΙΑΛΟΣ",
//                "ΛΕΧΑΙΝΑ-ΑΙΓΙΟ",
//                "ΛΕΧΑΙΝΑ-ΑΚΡΑΤΑ",
//                "ΛΕΧΑΙΝΑ-ΑΜΦΙΣΣΑ",
//                "ΛΕΧΑΙΝΑ-ΑΝΤΙΡΙΟ",
//                "ΛΕΧΑΙΝΑ-ΓΑΛΑΞΙΔΙ",
//                "ΛΕΧΑΙΝΑ-ΓΡΑΒΙΑ",
//                "ΛΕΧΑΙΝΑ-ΒΕΡΟΙΑ",
//                "ΛΕΧΑΙΝΑ-ΒΟΛΟΣ",
//                "ΛΕΧΑΙΝΑ-ΚΑΤΕΡΙΝΗ",
//                "ΛΕΧΑΙΝΑ-ΛΙΤΟΧΩΡΟ",
//                "ΛΕΧΑΙΝΑ-ΣΟΥΡΠΗ",
//                "ΛΕΧΑΙΝΑ-ΕΛΕΥΣΙΝΑ",
//                "ΛΕΧΑΙΝΑ-ΘΕΣΣΑΛΟΝΙΚΗ",
//                "ΛΕΧΑΙΝΑ-ΙΣΘΜΟΣ",
//                "ΛΕΧΑΙΝΑ-ΙΤΕΑ",
//                "ΛΕΧΑΙΝΑ-ΚΙΑΤΟ",
//                "ΛΕΧΑΙΝΑ-ΛΑΜΙΑ",
//                "ΛΕΧΑΙΝΑ-ΛΑΡΙΣΣΑ",
//                "ΛΕΧΑΙΝΑ-ΛΕΥΚΟΚΑΡΥΑ",
//                "ΛΕΧΑΙΝΑ-ΜΑΚΡΥΓΙΑΛΟΣ",
//                "ΛΕΧΑΙΝΑ-ΜΕΓΑΡΑ",
//                "ΛΕΧΑΙΝΑ-ΜΠΡΑΛΟΣ",
//                "ΛΕΧΑΙΝΑ-ΝΑΥΠΑΚΤΟΣ",
//                "ΛΕΧΑΙΝΑ-ΠΛΑΤΑΜΩΝΑΣ",
//                "ΛΕΧΑΙΝΑ-ΣΤΥΛΙΔΑ",
//                "ΛΕΧΑΙΝΑ-ΤΕΜΠΗ",
//                "ΠΥΡΓΟΣ-ΑΓΧΙΑΛΟΣ",
//                "ΠΥΡΓΟΣ-ΑΘΗΝΑ",
//                "ΠΥΡΓΟΣ-ΑΙΓΙΟ",
//                "ΠΥΡΓΟΣ-ΑΚΡΑΤΑ",
//                "ΠΥΡΓΟΣ-ΑΜΦΙΣΣΑ",
//                "ΠΥΡΓΟΣ-ΑΝΤΙΡΙΟ",
//                "ΠΥΡΓΟΣ-ΓΑΛΑΞΙΔΙ",
//                "ΠΥΡΓΟΣ-ΓΡΑΒΙΑ",
//                "ΠΥΡΓΟΣ-ΒΕΡΟΙΑ",
//                "ΠΥΡΓΟΣ-ΒΟΛΟΣ",
//                "ΠΥΡΓΟΣ-ΚΑΤΕΡΙΝΗ",
//                "ΠΥΡΓΟΣ-ΛΙΤΟΧΩΡΟ",
//                "ΠΥΡΓΟΣ-ΣΟΥΡΠΗ",
//                "ΠΥΡΓΟΣ-ΕΛΕΥΣΙΝΑ",
//                "ΠΥΡΓΟΣ-ΘΕΣΣΑΛΟΝΙΚΗ",
//                "ΠΥΡΓΟΣ-ΙΣΘΜΟΣ",
//                "ΠΥΡΓΟΣ-ΙΤΕΑ",
//                "ΠΥΡΓΟΣ-ΚΙΑΤΟ",
//                "ΠΥΡΓΟΣ-ΛΑΜΙΑ",
//                "ΠΥΡΓΟΣ-ΛΑΡΙΣΣΑ",
//                "ΠΥΡΓΟΣ-ΛΕΥΚΟΚΑΡΥΑ",
//                "ΠΥΡΓΟΣ-ΜΑΚΡΥΓΙΑΛΟΣ",
//                "ΠΥΡΓΟΣ-ΜΕΓΑΡΑ",
//                "ΠΥΡΓΟΣ-ΜΠΡΑΛΟΣ",
//                "ΠΥΡΓΟΣ-ΝΑΥΠΑΚΤΟΣ",
//                "ΠΥΡΓΟΣ-ΠΛΑΤΑΜΩΝΑΣ",
//                "ΠΥΡΓΟΣ-ΣΤΥΛΙΔΑ",
//                "ΠΥΡΓΟΣ-ΤΕΜΠΗ",
//                "ΠΥΡΓΟΣ-ΤΡΙΠΟΛΗ"
//        );
//
//
//        int i;
//        for(i=0;i<routeList.size();i++){
//
//            List<String> myList = Arrays.asList("00","15","30","45");
//            int k = 0;
//            String s1="0",s2="0",s3="0";
//            while(k<12){
//                String h1 = String.valueOf(ThreadLocalRandom.current().nextInt(6, 11 + 1));
//                String h2 = String.valueOf(ThreadLocalRandom.current().nextInt(12, 17 + 1));
//                String h3 = String.valueOf(ThreadLocalRandom.current().nextInt(18, 24 + 1));
//                if(Integer.parseInt(h1)/10<1){
//                    h1 = "0"+ String.valueOf(h1);
//                }
//                if(Integer.parseInt(h2)/10<1){
//                    h2 = "0"+ String.valueOf(h2);
//                }
//                if(Integer.parseInt(h3)/10<1){
//                    h3 = "0"+ String.valueOf(h3);
//                }
//                Object m1 = myList.get(new Random().nextInt(myList.size()));
//                Object m2 = myList.get(new Random().nextInt(myList.size()));
//                Object m3 = myList.get(new Random().nextInt(myList.size()));
//                s1 = h1+":"+m1;
//                s2 = h2+":"+m2;
//                s3 = h3+":"+m3;
//                k+=1;
//            }
//            List<String> arr = Arrays.asList(
//                    s1,
//                    s2,
//                    s3);
//            Map<String, List<String>> city = new HashMap<>();
//            city.put("ΩΡΑ", arr);
//            db.collection("Routes").document(routeList.get(i))
//                    .set(city)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d("test", "DocumentSnapshot successfully written!");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w("test", "Error writing document", e);
//                        }
//                    });
//        }

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }
        });

        btnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RouteActivity.class));
            }
        });

    }
}