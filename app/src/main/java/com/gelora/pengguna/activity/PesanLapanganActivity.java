package com.gelora.pengguna.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gelora.pengguna.R;
import com.gelora.pengguna.adapter.JamSewaAdapter;
import com.gelora.pengguna.interfaces.OnJamClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import static com.gelora.pengguna.adapter.LapanganAdapter.GAMBAR_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.HARGA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.ID_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.JENIS_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.KATEGORI_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.NAMA_LAPANGAN;

public class PesanLapanganActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener , OnJamClickListener {

    TextView namaLapangan, kategoriLapangan, jenisLapangan, hargaLapangan, pilihTanggalLapangan, tanggalLapanganReview, jamLapanganReview, hargaLapanganReview;
    ImageView gambharLapangan, backButton;
    DatabaseReference ref, ketersedianLapanganRef;
    Button datePicker;
    String idLapanganIntent, namaLapanganIntent, gambarLapanganIntent, kategoriLapanganIntent, jenisLapanganIntent;
    long hargaLapanganIntent;
    ArrayList<String> jamArrayList;
    ArrayList<String> jamSewaArrayList;
    RecyclerView jamSewaRecycler;
    String textA;
    Locale locale = new Locale("id", "ID");
    NumberFormat n = NumberFormat.getCurrencyInstance(locale);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan_lapangan);
        namaLapangan = findViewById(R.id.nama_lapangan_pesan);
        kategoriLapangan = findViewById(R.id.kategori_lapangan_pesan);
        jenisLapangan = findViewById(R.id.jenmis_lapangan_pesan);
        hargaLapangan = findViewById(R.id.harga_sewa_pesan);
        pilihTanggalLapangan = findViewById(R.id.tanggal_text);
        tanggalLapanganReview = findViewById(R.id.tanggalReview_text);
        jamLapanganReview = findViewById(R.id.jamReview_text);
        hargaLapanganReview = findViewById(R.id.hargaSewa_review_text);
        gambharLapangan = findViewById(R.id.img_lapangan_pesan);
        backButton = findViewById(R.id.back_arrow);
        datePicker = findViewById(R.id.pilih_tanggal_button);
        jamSewaRecycler = findViewById(R.id.jam_sewa_recycler);

        // ambil data dari Intent sebelumnya
        Intent intent = getIntent();
        idLapanganIntent = intent.getStringExtra(ID_LAPANGAN);
        namaLapanganIntent = intent.getStringExtra(NAMA_LAPANGAN);
        gambarLapanganIntent = intent.getStringExtra(GAMBAR_LAPANGAN);
        kategoriLapanganIntent = intent.getStringExtra(KATEGORI_LAPANGAN);
        jenisLapanganIntent = intent.getStringExtra(JENIS_LAPANGAN);
        hargaLapanganIntent = intent.getLongExtra(HARGA_LAPANGAN,0);
        //
        jamSewaRecycler.setHasFixedSize(true);
        jamSewaRecycler.setLayoutManager(new LinearLayoutManager(this));
        // set data dari intent
        Glide.with(this)
                .load(gambarLapanganIntent)
                .centerCrop()
                .placeholder(R.drawable.img_placeholder_lapangan)
                .into(gambharLapangan);
        namaLapangan.setText(namaLapanganIntent);
        kategoriLapangan.setText(kategoriLapanganIntent);
        jenisLapangan.setText(jenisLapanganIntent);
        String hargaText = n.format(hargaLapanganIntent);
        String hargaSetText = hargaText.replaceAll(",00","").replaceAll("Rp", "Rp. ");
        hargaLapangan.setText(hargaSetText);
        ref = FirebaseDatabase.getInstance().getReference("lapangan/id_lapangan").child(idLapanganIntent).child("jam_sewa");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    jamArrayList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        jamArrayList.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // set data default
        pilihTanggalLapangan.setVisibility(View.INVISIBLE);
        tanggalLapanganReview.setVisibility(View.INVISIBLE);
        jamLapanganReview.setVisibility(View.VISIBLE);
        hargaLapanganReview.setVisibility(View.VISIBLE);
        jamLapanganReview.setText(null);
        hargaLapanganReview.setText(null);

        // set onClickListener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                this,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.YEAR)
        );
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (month == 0) {
            month = 1;
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        String tanggal = "" + dayOfMonth + "/" + month + "/" + year;
        String dateString = DateFormat.getDateInstance(DateFormat.FULL, locale).format(c.getTime());
        pilihTanggalLapangan.setText(tanggal);
        pilihTanggalLapangan.setVisibility(View.VISIBLE);
        tanggalLapanganReview.setVisibility(View.VISIBLE);
        tanggalLapanganReview.setText(dateString);
        ketersedianLapanganRef = FirebaseDatabase.getInstance().getReference("ketersediaan_lapangan").child(idLapanganIntent).child(dateString);
        ketersedianLapanganRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jamSewaArrayList = new ArrayList<>();
                if (!snapshot.exists()){
                    for (int i = 0; i < jamArrayList.size(); i++){
                        ketersedianLapanganRef.child(jamArrayList.get(i)).setValue("tersedia");
                    }
                    System.out.println(jamArrayList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PesanLapanganActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    jamSewaRecycler.setLayoutManager(linearLayoutManager);
                    JamSewaAdapter jamSewaAdapter = new JamSewaAdapter(PesanLapanganActivity.this, jamArrayList, PesanLapanganActivity.this);
                    jamSewaRecycler.setAdapter(jamSewaAdapter);
                } else {
                    for (DataSnapshot ds : snapshot.getChildren()){
                        if (ds.getValue().equals("tersedia")){
                            jamSewaArrayList.add(ds.getKey());
                        }
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PesanLapanganActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    jamSewaRecycler.setLayoutManager(linearLayoutManager);
                    JamSewaAdapter jamSewaAdapter = new JamSewaAdapter(PesanLapanganActivity.this, jamSewaArrayList, PesanLapanganActivity.this);
                    jamSewaRecycler.setAdapter(jamSewaAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void jamReview(ArrayList<String> jamList) {
        Collections.sort(jamList);
        if (jamList.isEmpty()){
            textA = "";

        } else  {
            for (int i =  0; i < jamList.size(); i++) {
                textA = jamList.toString();
            }
        }
        String str = textA.replaceAll("\\[", "").replaceAll("\\]", "");
        jamLapanganReview.setText(str);
        int multiplier = jamList.size();
        long harga = hargaLapanganIntent;
        long totalHarga = harga * multiplier;
        String s = n.format(totalHarga);
        String a = s.replaceAll(",00","").replaceAll("Rp", "Rp. ");
        System.out.println(a);
        hargaLapanganReview.setText(a);
    }
}
