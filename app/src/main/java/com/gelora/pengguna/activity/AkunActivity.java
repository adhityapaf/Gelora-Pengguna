package com.gelora.pengguna.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gelora.pengguna.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AkunActivity extends AppCompatActivity {
    private static final String TAG = "AkunActivity";
    Button logout, simpanPerubahan, lapanganFavorit, tentangKami;
    EditText namaMitra, emailMitra, passwordMitra;
    DatabaseReference userRef;
    String nama, email, passwrd;
    ImageView backButton;
    AuthCredential credential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        namaMitra = findViewById(R.id.editNamaField);
        emailMitra = findViewById(R.id.editEmailField);
        passwordMitra = findViewById(R.id.editPasswordField);
        simpanPerubahan = findViewById(R.id.editSimpanPerubahanButton);
        lapanganFavorit = findViewById(R.id.lapanganFavoritButton);
        backButton = findViewById(R.id.back_arrow);
        tentangKami = findViewById(R.id.tentangKamiButton);

        loadProfileUser();
        logout = findViewById(R.id.logoutButton);
        simpanPerubahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanPerubahan.setBackground(getDrawable(R.drawable.disabled_green_stroke_rectangle));
                simpanPerubahan.setText("Menyimpan Perubahan....");
                nama = namaMitra.getText().toString();
                passwrd = passwordMitra.getText().toString();
                if (TextUtils.isEmpty(nama)) {
                    namaMitra.setError("Nama tidak boleh kosong.");
                    return;
                }
                if (TextUtils.isEmpty(passwrd)) {
                    passwordMitra.setError("Password tidak boleh kosong.");
                    return;
                }
                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential);
                FirebaseAuth.getInstance().getCurrentUser().updatePassword(passwrd).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userRef.child("nama").setValue(nama);
                        userRef.child("password").setValue(passwrd);
                        simpanPerubahan.setBackground(getDrawable(R.drawable.button_green_rounded_5dp));
                        simpanPerubahan.setText("Simpan Perubahan");
                        Toast.makeText(AkunActivity.this, "Berhasil simpan perubahan akun", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        simpanPerubahan.setBackground(getDrawable(R.drawable.button_green_rounded_5dp));
                        simpanPerubahan.setText("Simpan Perubahan");
                        Toast.makeText(AkunActivity.this, "Gagal Mengubah Profil, Coba lagi.", Toast.LENGTH_LONG).show();
                        return;
                    }
                });
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AkunActivity.this, SplashScreen.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lapanganFavorit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AkunActivity.this, LapanganFavoritActivity.class));
            }
        });

        tentangKami.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AkunActivity.this, TentangKami.class));
            }
        });
    }
    void loadProfileUser() {
        userRef.child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.getValue().toString();
                emailMitra.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userRef.child("nama").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nama = snapshot.getValue().toString();
                namaMitra.setText(nama);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userRef.child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                passwrd = snapshot.getValue().toString();
                passwordMitra.setText(passwrd);
                credential = EmailAuthProvider.getCredential(email,passwrd);
                Log.d(TAG, "AuthCred: "+credential);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
