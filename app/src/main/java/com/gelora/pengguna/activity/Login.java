package com.gelora.pengguna.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gelora.pengguna.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    EditText emailField, passwordField;
    ProgressBar progressBar;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailField = findViewById(R.id.emailPelangganField);
        passwordField = findViewById(R.id.passwordPelangganField);
        progressBar = findViewById(R.id.progressbar_circle);
        login = findViewById(R.id.loginSubmitButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailField.setError("Masukan Email Mitra Anda");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordField.setError("Masukkan Password Anda");
                    return;
                }
                if (password.length() < 6) {
                    passwordField.setError("Password Minimal 6 Karakter");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            databaseReference.child(FirebaseAuth.getInstance().getUid()).child("role").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue().toString().equals("Pengguna")) {
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                        Toast.makeText(Login.this, "Berhasil Masuk!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    } else {
                                        Toast.makeText(Login.this, "Bukan Akun Pengguna", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            Toast.makeText(Login.this, "Ups terjadi kesalahan : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}