package com.gelora.pengguna.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_PESAN_USER;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TOTAL_HARGA;
import static com.gelora.pengguna.adapter.LapanganAdapter.NAMA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.UID_MITRA;

public class DetailPesananActivity extends AppCompatActivity {
    TextView idTransaksi, tanggalPesan, waktuPesan,  namaPemesan, namaLapangan, totalHarga, statusLabel, statusField, jadwalLapanganField;
    Button buktiTransferButton, batalkanButton;
    String idTransaksiIntent,namaPemesanIntent, buktiPembayaranIntent, jamPesanIntent, tanggalPesanIntent, statusPesanIntent, namaLapanganIntent, alasanPesananIntent, UIDMitraIntent, tanggalPesanUserIntent;
    int totalHargaIntent;
    String forUploadText = "belum ada";
    String alasanDefault = "Tidak Ada";
    Locale locale = new Locale("id", "ID");
    NumberFormat n = NumberFormat.getCurrencyInstance(locale);
    String s, a;
    DatabaseReference hapusRef;
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
        buktiTransferButton = findViewById(R.id.lihatBuktiTransfer_button);
        batalkanButton = findViewById(R.id.tolak_button);
        statusLabel = findViewById(R.id.alasan_ditolak_label);
        statusField = findViewById(R.id.alasan_ditolak_field);
        jadwalLapanganField = findViewById(R.id.jadwalLapanganField);
        retrieveIntent();
        settingText();
        if (statusPesanIntent.equals("Diterima")){
            statusLabel.setText("ALASAN DITERIMA");
            statusField.setTextColor(Color.parseColor("#34A853"));
            statusField.setText(alasanPesananIntent);
        } else if (statusPesanIntent.equals("Ditolak")){
            statusLabel.setText("ALASAN DITOLAK");
            statusField.setText(alasanPesananIntent);
            statusField.setTextColor(Color.RED);
        }
        if (buktiPembayaranIntent.equals(forUploadText)){
            buktiTransferButton.setText("Upload Bukti Pembayaran");
        }

        if (alasanPesananIntent.equals(alasanDefault)){
            statusField.setText(statusPesanIntent);
        } else {
            statusField.setText(alasanPesananIntent);
        }

        hapusRef = FirebaseDatabase.getInstance().getReference("pesanan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tanggalPesanUserIntent).child("id_pesanan").child(idTransaksiIntent);
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
                    intent.putExtra(TANGGAL_PESAN_USER, tanggalPesanUserIntent);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DetailPesananActivity.this, ImagePreviewActivity.class);
                    intent.putExtra(BUKTI_PEMBAYARAN, buktiPembayaranIntent);
                    startActivity(intent);
                }
            }
        });

        batalkanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(DetailPesananActivity.this).create();
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailPesananActivity.this);
                builder.setTitle("Batalkan Pesanan");
                builder.setMessage("Apakah Anda yakin untuk membatalkan pesanan ini?");
                builder.setPositiveButton("Batalkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hapusRef.removeValue();
                        Toast.makeText(DetailPesananActivity.this, "Pesanan dibatalkan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                });
                builder.show();
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
        tanggalPesanUserIntent = intent.getStringExtra(TANGGAL_PESAN_USER);
        s = n.format(totalHargaIntent);
        a = s.replaceAll(",00", "").replaceAll("Rp", "Rp. ");
    }

    private void settingText() {
        idTransaksi.setText(idTransaksiIntent);
        tanggalPesan.setText(tanggalPesanUserIntent);
        jadwalLapanganField.setText(tanggalPesanIntent);
        waktuPesan.setText(jamPesanIntent);
        namaPemesan.setText(namaPemesanIntent);
        namaLapangan.setText(namaLapanganIntent);
        totalHarga.setText(a);
        statusField.setText(alasanPesananIntent);
        statusField.setTextColor(Color.BLACK);
        statusLabel.setText("STATUS PESANAN");
        if (statusPesanIntent.equals("Belum Upload Bukti")){
            batalkanButton.setVisibility(View.VISIBLE);
        } else {
            batalkanButton.setVisibility(View.GONE);
        }
    }

}