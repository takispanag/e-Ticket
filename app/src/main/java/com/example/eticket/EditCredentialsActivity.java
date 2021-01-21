package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditCredentialsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_credentials);

        getSupportActionBar().hide();

        TextView email_textView = findViewById(R.id.email_edit);
        TextView password_textView = findViewById(R.id.edit_password);
        TextView new_password_textView = findViewById(R.id.newPassword);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Button edit_credentials = findViewById(R.id.editCredentialsButton);
        edit_credentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthCredential credential = EmailAuthProvider.getCredential(email_textView.getText().toString(), password_textView.getText().toString());

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(new_password_textView.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EditCredentialsActivity.this, getString(R.string.allagiPassword), Toast.LENGTH_SHORT).show();
                                                Intent profile = new Intent(EditCredentialsActivity.this,ProfileActivity.class);
                                                startActivity(profile);
                                            } else {
                                                Toast.makeText(EditCredentialsActivity.this, getString(R.string.errorAllagiPassword), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(EditCredentialsActivity.this, getString(R.string.lathosStoixeia), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}