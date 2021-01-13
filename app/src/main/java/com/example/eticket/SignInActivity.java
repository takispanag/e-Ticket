package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().hide();
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        Button Btn = findViewById(R.id.login);

        // Set on Click Listener on Sign-in button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginUserAccount();
            }
        });
    }

    private void loginUserAccount()
    {

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // validations for input email and password
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if(!pattern.matcher(emailTextView.getText().toString()).find() || emailTextView.getText().toString().equals("") || emailTextView.getText().toString().equals(null)){
            Toast.makeText(SignInActivity.this, "Παρακαλώ πληκτρολογήστε σωστά το email σας.", Toast.LENGTH_SHORT).show();
        }
        else if(passwordTextView.getText().toString().equals("") || passwordTextView.getText().toString().equals(null)){
            Toast.makeText(SignInActivity.this, "Παρακαλώ πληκτρολογήστε το password σας.", Toast.LENGTH_SHORT).show();
        }
        else if(passwordTextView.getText().toString().trim().length() < 6) {
            Toast.makeText(SignInActivity.this, "Το μέγεθος του κωδικού πρέπει να είναι τουλάχιστον 6.", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(
                                        @NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),
                                                "Επιτυχής σύνδεση!",
                                                Toast.LENGTH_LONG)
                                                .show();

                                        // if sign-in is successful
                                        // intent to home activity
                                        Intent intent
                                                = new Intent(SignInActivity.this,
                                                RouteActivity.class);
                                        startActivity(intent);
                                    }

                                    else {

                                        // sign-in failed
                                        Toast.makeText(getApplicationContext(),
                                                "Η σύνδεση απέτυχε! Παρακαλώ εισάγεται ξανά τα στοιχεία σας.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
        }
        // signin existing user
    }
}