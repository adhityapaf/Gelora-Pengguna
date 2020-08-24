package com.gelora.pengguna.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gelora.pengguna.R;
import com.gelora.pengguna.adapter.LapanganAdapter;
import com.gelora.pengguna.fragment.LapanganFragment;
import com.gelora.pengguna.fragment.HomeFragment;
import com.gelora.pengguna.fragment.OrderFragment;
import com.gelora.pengguna.model.LapanganData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import static com.gelora.pengguna.adapter.LapanganAdapter.GAMBAR_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.HARGA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.ID_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.JENIS_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.KATEGORI_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.NAMA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.UID_MITRA;

public class MainActivity extends AppCompatActivity {
    private long backPressedTime;
    ChipNavigationBar bottomNav;
    FragmentManager  fragmentManager;
    CardView akunButton;
    ProgressBar progressBar;
    private static final String TAG = "MainActivity";
    DatabaseReference ref, lapanganRef;
    TextView namaPemain;
    EditText searchbox;
    RecyclerView searchRecycler;
    FrameLayout fragmentContainer;
    FirebaseRecyclerOptions<LapanganData> options;
    FirebaseRecyclerAdapter<LapanganData, LapanganAdapter.ViewHolder> adapter;
    ImageView searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);
        akunButton = findViewById(R.id.akunButton);
        namaPemain = findViewById(R.id.username);
        progressBar = findViewById(R.id.progressbarlingkaran);
        searchbox = findViewById(R.id.searchbox_lapangan);
        searchButton = findViewById(R.id.searchIconButton);
        searchRecycler = findViewById(R.id.searchRecycler);
        searchRecycler.setHasFixedSize(true);
        searchRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        searchRecycler.setVisibility(View.GONE);
        fragmentContainer = findViewById(R.id.fragment_container);
        ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("nama");
        lapanganRef = FirebaseDatabase.getInstance().getReference("lapangan").child("id_lapangan");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                namaPemain.setText(snapshot.getValue().toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        akunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AkunActivity.class));
            }
        });
        if (savedInstanceState==null){
            bottomNav.setItemSelected(R.id.home, true);
            fragmentManager =getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        }
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.lapangan:
                        fragment = new LapanganFragment();
                        break;
                    case R.id.list:
                        fragment = new OrderFragment();
                        break;
                }
                if (fragment!=null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                } else {
                    Log.d(TAG, "onItemSelected: Error in Creating Fragment");
                }
            }
        });

        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()){
                    fragmentContainer.setVisibility(View.GONE);
                    searchRecycler.setVisibility(View.VISIBLE);
                    setAdapter(s.toString());
                    Glide.with(getApplicationContext())
                            .load(R.drawable.ic_baseline_clear_24)
                            .into(searchButton);
                    searchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchbox.setText("");
                        }
                    });
                } else {
                    fragmentContainer.setVisibility(View.VISIBLE);
                    searchRecycler.setVisibility(View.GONE);
                    Glide.with(getApplicationContext())
                            .load(R.drawable.ic_search_black_24dp)
                            .into(searchButton);
                }
            }
        });
    }

    private void setAdapter(String toString) {
        String searchText = toString.toUpperCase();
        Query query = lapanganRef.orderByChild("searchString").startAt(searchText).endAt(searchText+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<LapanganData>().setQuery(query, LapanganData.class).build();
        adapter = new FirebaseRecyclerAdapter<LapanganData, LapanganAdapter.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final LapanganAdapter.ViewHolder holder, final int position, @NonNull final LapanganData model) {
                Glide.with(getApplicationContext())
                        .load(model.getGambar_lapangan())
                        .centerCrop()
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.gambarLapangan);
                holder.namaLapangan.setText(model.getNama_lapangan());
                holder.kategoriLapangan.setText(model.getKategori_lapangan());
                holder.jenisLapangan.setText(model.getJenis_lapangan());
                holder.hargaLapangan.setText("Rp. "+model.getHarga());
                holder.lapanganCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PesanLapanganActivity.class);
                        String idLapanganStr, namaLapanganStr, kategoriLapanganStr, jenisLapanganStr, gambarLapanganStr;
                        gambarLapanganStr = model.getGambar_lapangan();
                        namaLapanganStr = holder.namaLapangan.getText().toString();
                        kategoriLapanganStr = holder.kategoriLapangan.getText().toString();
                        jenisLapanganStr = holder.jenisLapangan.getText().toString();
                        long hargaLapanganStr = model.getHarga();
                        idLapanganStr = model.getId_lapangan();
                        intent.putExtra(ID_LAPANGAN, idLapanganStr);
                        intent.putExtra(NAMA_LAPANGAN, namaLapanganStr);
                        intent.putExtra(GAMBAR_LAPANGAN, gambarLapanganStr);
                        intent.putExtra(KATEGORI_LAPANGAN, kategoriLapanganStr);
                        intent.putExtra(JENIS_LAPANGAN, jenisLapanganStr);
                        intent.putExtra(HARGA_LAPANGAN, hargaLapanganStr);
                        intent.putExtra(UID_MITRA, model.getUID_Mitra());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public LapanganAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lapangan_items, parent, false);
                return new LapanganAdapter.ViewHolder(view);
            }
        };
        adapter.startListening();
        searchRecycler.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tekan kembali lagi untuk keluar", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();

    }
}
