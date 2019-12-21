package com.imsaddam.luxevents.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.imsaddam.luxevents.R;
import com.imsaddam.luxevents.models.User;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText emailInput, passwordInput, nameInput;
    Button signupBtn;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();



        mDatabase = FirebaseDatabase.getInstance().getReference();
        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        nameInput = findViewById(R.id.input_name);
        signupBtn = findViewById(R.id.signupbtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 registerUser(nameInput.getText().toString(), emailInput.getText().toString(),passwordInput.getText().toString());
            }
        });


    }


    private void registerUser(String name, final String email, String password){

        mAuth.createUserWithEmailAndPassword( email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = task.getResult().getUser();
                            User user = new User(nameInput.getText().toString(),email);
                            mDatabase.child("users").child(firebaseUser.getUid()).setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Registration Successful", "createUserWithEmail:success");
                                            Intent logingIntent = new Intent(getApplicationContext(),LoginActivity.class);
                                            startActivity(logingIntent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, "Authentication failed." + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Registration Failed", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
