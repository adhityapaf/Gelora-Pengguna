package com.gelora.pengguna.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gelora.pengguna.R;
import com.gelora.pengguna.adapter.LapanganAdapter;
import com.gelora.pengguna.model.LapanganData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LapanganFavoritActivity extends AppCompatActivity {
    ImageView backButton;
    TextView namaKategori;
    RecyclerView lapanganFavoritRecycler;
    Context mContext;
    DatabaseReference ref, favRef;
    ArrayList<LapanganData> list;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapangan_favorit);
        lapanganFavoritRecycler = findViewById(R.id.lapanganFavoritRecycler);
        progressBar = findViewById(R.id.progressbarlingkaran);
        progressBar.setVisibility(View.VISIBLE);
        backButton = findViewById(R.id.back_arrow);
        list = new ArrayList<>();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        ref = FirebaseDatabase.getInstance().getReference("lapangan").child("id_lapangan");
        favRef = FirebaseDatabase.getInstance().getReference("favorit_lapangan").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        lapanganFavoritRecycler.setHasFixedSize(true);
        lapanganFavoritRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        list.clear();
        readData();
    }

    void readData(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list = new ArrayList<>();
                    for (final DataSnapshot ds : snapshot.getChildren()){
                        favRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds2 : snapshot.getChildren()){
                                    if (ds.child("id_lapangan").getValue().equals(ds2.getKey())){
                                        LapanganData lp = ds.getValue(LapanganData.class);
                                        list.add(lp);
                                    }
                                    favRef.keepSynced(true);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    final LapanganAdapter lapanganAdapter = new LapanganAdapter(list, LapanganFavoritActivity.this);
                    lapanganFavoritRecycler.setAdapter(lapanganAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
