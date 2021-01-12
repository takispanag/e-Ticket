package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import com.example.eticket.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameTextView,emailTextView, passwordTextView, confPasswordTextView;
    private FirebaseAuth mAuth;
    CollectionReference db = FirebaseFirestore.getInstance().collection("UserInfo");
    CollectionReference db2 = FirebaseFirestore.getInstance().collection("UserSeats");


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        confPasswordTextView = findViewById(R.id.confPassword);
        Button Btn = findViewById(R.id.signUp);

        // Set on Click Listener on Sign-un button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //regex for email
                Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                if(nameTextView.getText().toString().equals("")|| nameTextView.getText().toString().equals(null)){
                    Toast.makeText(SignUpActivity.this, "Παρακαλώ πληκτρολογήστε το όνομά σας.", Toast.LENGTH_SHORT).show();
                }
                else if(!pattern.matcher(emailTextView.getText().toString()).find() || emailTextView.getText().toString().equals("") || emailTextView.getText().toString().equals(null)){
                    Toast.makeText(SignUpActivity.this, "Παρακαλώ πληκτρολογήστε σωστά το email σας.", Toast.LENGTH_SHORT).show();
                }
                else if(passwordTextView.getText().toString().equals("") || passwordTextView.getText().toString().equals(null)){
                    Toast.makeText(SignUpActivity.this, "Παρακαλώ πληκτρολογήστε το password σας.", Toast.LENGTH_SHORT).show();
                }
                else if(confPasswordTextView.getText().toString().equals("") || confPasswordTextView.getText().toString().equals(null)){
                    Toast.makeText(SignUpActivity.this, "Παρακαλώ πληκτρολογήστε το ξανά το password σας.", Toast.LENGTH_SHORT).show();
                }
                else{
                    signupUserAccount();
                }
            }

        });

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void signupUserAccount()
    {
        mAuth.createUserWithEmailAndPassword(emailTextView.getText().toString(),passwordTextView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Executors.newSingleThreadExecutor().execute(()->{
                                Map<String, Object> userSeats = new HashMap<>();
                                User user = new User(nameTextView.getText().toString(),emailTextView.getText().toString(),userSeats);
                                //create user
                                db.document(mAuth.getUid()).set(user);

                                //
                                db2.document(mAuth.getCurrentUser().getUid()).set(new HashMap<String, Object>());
                            });
                            // Sign in success, update UI with the signed-in user's information

                            startActivity(new Intent(getBaseContext(), RouteActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signup", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}