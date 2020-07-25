package com.gelora.pengguna.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gelora.pengguna.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Locale;

import static com.gelora.pengguna.activity.PesanLapanganActivity.ALASAN_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.BUKTI_PEMBAYARAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.ID_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.JAM_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.NAMA_PEMESAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.STATUS_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TOTAL_HARGA;
import static com.gelora.pengguna.adapter.LapanganAdapter.NAMA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.UID_MITRA;

public class DetailPesananActivity extends AppCompatActivity {
    TextView idTransaksi, tanggalPesan, waktuPesan,  namaPemesan, namaLapangan, totalHarga, statusPesanan;
    ImageView statusIcon;
    Button buktiTransferButton;
    String idTransaksiIntent,namaPemesanIntent, buktiPembayaranIntent, jamPesanIntent, tanggalPesanIntent, statusPesanIntent, namaLapanganIntent, alasanPesananIntent, UIDMitraIntent;
    int totalHargaIntent;
    String forUploadText = "belum ada";
    String alasanDefault = "Tidak Ada";
    Locale locale = new Locale("id", "ID");
    NumberFormat n = NumberFormat.getCurrencyInstance(locale);
    String s, a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);
        idTransaksi = findViewById(R.id.idTransaksi_field);
        tanggalPesan = findViewById(R.id.tanggalPesan_field);
        waktuPesan = findViewById(R.id.waktuPesan_field);
        namaPemesan = findViewById(R.id.namaPemesan_field);
        namaLapangan = findViewById(R.id.namaLapanganPesanan_field);
        totalHarga = findViewById(R.id.totalHarga_amount);
        statusPesanan = findViewById(R.id.statusPesananText);
        statusIcon = findViewById(R.id.statusIcon);
        buktiTransferButton = findViewById(R.id.lihatBuktiTransfer_button);

        retrieveIntent();
        settingText();
        if (statusPesanan.getText().toString().equals("Diterima")){
            statusPesanan.setTextColor(Color.parseColor("#34A853"));
            statusIcon.setVisibility(View.VISIBLE);
        } else if (statusPesanan.getText().toString().equals("Ditolak")){
            statusPesanan.setTextColor(Color.RED);
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_ditolak)
                    .into(statusIcon);
        }
        if (buktiPembayaranIntent.equals(forUploadText)){
            buktiTransferButton.setText("Upload Bukti Pembayaran");
        }

        if (alasanPesananIntent.equals(alasanDefault)){
            statusPesanan.setText(statusPesanIntent);
        } else {
            statusPesanan.setText(alasanPesananIntent);
        }

        // membuat tampilan seperti pop up
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width), (int)(height*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.x = 0;
        params.y = 0;

        getWindow().setAttributes(params);

        buktiTransferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buktiPembayaranIntent.equals(forUploadText)){
                    Intent intent = new Intent(DetailPesananActivity.this, UploadBuktiPembayaranActivity.class);
                    intent.putExtra(ID_PESANAN, idTransaksiIntent);
                    intent.putExtra(NAMA_PEMESAN, namaPemesanIntent);
                    intent.putExtra(TOTAL_HARGA, totalHargaIntent);
                    intent.putExtra(BUKTI_PEMBAYARAN, buktiPembayaranIntent);
                    intent.putExtra(JAM_PESANAN, jamPesanIntent);
                    intent.putExtra(TANGGAL_PESANAN, tanggalPesanIntent);
                    intent.putExtra(STATUS_PESANAN, statusPesanIntent);
                    intent.putExtra(NAMA_LAPANGAN, namaLapanganIntent);
                    intent.putExtra(ALASAN_PESANAN, alasanPesananIntent);
                    intent.putExtra(UID_MITRA, UIDMitraIntent);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DetailPesananActivity.this, ImagePreviewActivity.class);
                    intent.putExtra(BUKTI_PEMBAYARAN, buktiPembayaranIntent);
                    startActivity(intent);
                }
            }
        });
    }

    private void retrieveIntent() {
        Intent intent = getIntent();
        idTransaksiIntent = intent.getStringExtra(ID_PESANAN);
        namaPemesanIntent = intent.getStringExtra(NAMA_PEMESAN);
        totalHargaIntent = intent.getIntExtra(TOTAL_HARGA, 0);
        buktiPembayaranIntent = intent.getStringExtra(BUKTI_PEMBAYARAN);
        jamPesanIntent = intent.getStringExtra(JAM_PESANAN);
        tanggalPesanIntent = intent.getStringExtra(TANGGAL_PESANAN);
        statusPesanIntent = intent.getStringExtra(STATUS_PESANAN);
        namaLapanganIntent = intent.getStringExtra(NAMA_LAPANGAN);
        alasanPesananIntent = intent.getStringExtra(ALASAN_PESANAN);
        UIDMitraIntent = intent.getStringExtra(UID_MITRA);
        s = n.format(totalHargaIntent);
        a = s.replaceAll(",00", "").replaceAll("Rp", "Rp. ");
    }

    private void settingText() {
        idTransaksi.setText(idTransaksiIntent);
        tanggalPesan.setText(tanggalPesanIntent);
        waktuPesan.setText(jamPesanIntent);
        namaPemesan.setText(namaPemesanIntent);
        namaLapangan.setText(namaLapanganIntent);
        totalHarga.setText(a);
        statusPesanan.setText(statusPesanIntent);
        statusPesanan.setTextColor(Color.BLACK);
        statusIcon.setVisibility(View.INVISIBLE);
    }

}