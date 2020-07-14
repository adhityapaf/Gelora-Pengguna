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
import com.gelora.pengguna.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference users;
    FirebaseAuth firebaseAuth;
    EditText namaField, emailField, passwordField;
    Button registerButton;
    ProgressBar progressBar;
    String role = "Pengguna";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        namaField = findViewById(R.id.namaPelangganField);
        emailField = findViewById(R.id.emailPelangganField);
        passwordField = findViewById(R.id.passwordPelangganField);
        progressBar = findViewById(R.id.progressbar_circle);
        registerButton = findViewById(R.id.daftarSubmitButton);
        firebaseAuth = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();
                final String nama = namaField.getText().toString();

                if (TextUtils.isEmpty(nama)) {
                    namaField.setError("Nama Tidak Boleh Kosong");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailField.setError("Email Tidak Boleh Kosong");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordField.setError("Password tidak boleh kosong");
                    return;
                }
                if (password.length() < 6){
                    passwordField.setError("Password Minimal 6 Karakter");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(
                                    nama,
                                    email,
                                    password,
                                    role
                            );
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Register.this, "Registreasi Berhasil!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Register.this, "Ups Terjadi kesalahan :(", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            finish();
                            Toast.makeText(Register.this, "Akun terdaftar! Silakan login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Login.class));
                        } else {
                            Toast.makeText(Register.this, "Ups ada kesalahan : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });
    }
}
