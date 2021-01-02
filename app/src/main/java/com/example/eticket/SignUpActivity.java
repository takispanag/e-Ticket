package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.example.eticket.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameTextView,emailTextView, passwordTextView,confpasswordTextView;
    private Button Btn;
    private FirebaseAuth mAuth;
    CollectionReference db = FirebaseFirestore.getInstance().collection("UserInfo");


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        confpasswordTextView = findViewById(R.id.confPassword);
        Btn = findViewById(R.id.signUp);

        // Set on Click Listener on Sign-un button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v)
            {
                signupUserAccount();
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
                                User user = new User(nameTextView.getText().toString(),emailTextView.getText().toString());
                                db.document(mAuth.getUid()).set(user);
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