package com.gelora.pengguna.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gelora.pengguna.R;
import com.gelora.pengguna.model.PesananData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gelora.pengguna.activity.PesanLapanganActivity.ALASAN_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.BUKTI_PEMBAYARAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.ID_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.JAM_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.NAMA_PEMESAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.STATUS_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_PESAN_USER;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TOTAL_HARGA;
import static com.gelora.pengguna.activity.PesanLapanganActivity.UID_PELANGGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.ID_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.NAMA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.UID_MITRA;

public class UploadBuktiPembayaranActivity extends AppCompatActivity {
    private static final String TAG = "UploadBuktiPembayaranAc";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    ImageView gambarBukti, backButton;
    Button pilihGambarButton, uploadButton;
    ProgressBar progressBar;
    DatabaseReference ref, totalPesananRef;
    String idPesanan,namaPemesan,bukti_pembayaran, jam_pesan, tanggalPesanan, statusPesanan, nama_lapangan, alasan_status, UIDMItra, UIDPelanggan, tanggalPesanUser, idLapangan;
    String statusPembayaran = "Sudah Upload Bukti";
    int total_harga;
    int total_pesanan;
    StorageTask mUploadTask;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bukti_pembayaran);
        gambarBukti = findViewById(R.id.bukti_image);
        pilihGambarButton = findViewById(R.id.pilihGambarButton);
        uploadButton = findViewById(R.id.uploadButton);
        progressBar = findViewById(R.id.horizontalProgressBar);
        backButton = findViewById(R.id.back_arrow);

        // ambil data dari intent sebelumnya
        Intent intent = getIntent();
        idPesanan = intent.getStringExtra(ID_PESANAN);
        namaPemesan = intent.getStringExtra(NAMA_PEMESAN);
        total_harga = intent.getIntExtra(TOTAL_HARGA, 0);
        bukti_pembayaran = intent.getStringExtra(BUKTI_PEMBAYARAN);
        jam_pesan = intent.getStringExtra(JAM_PESANAN);
        tanggalPesanan = intent.getStringExtra(TANGGAL_PESANAN);
        statusPesanan = intent.getStringExtra(STATUS_PESANAN);
        nama_lapangan = intent.getStringExtra(NAMA_LAPANGAN);
        alasan_status = intent.getStringExtra(ALASAN_PESANAN);
        UIDMItra = intent.getStringExtra(UID_MITRA);
        UIDPelanggan = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tanggalPesanUser = intent.getStringExtra(TANGGAL_PESAN_USER);
        idLapangan = intent.getStringExtra(ID_LAPANGAN);
        totalPesananRef = FirebaseDatabase.getInstance().getReference("pesanan_pemilik").child(UIDMItra).child("total_pesanan");
        readDataFirebase();

        gambarBukti.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        pilihGambarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gambarBukti.isShown()){
                    Toast.makeText(UploadBuktiPembayaranActivity.this, "Silakan Pilih Gambar Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(UploadBuktiPembayaranActivity.this, "Proses upload gambar sedang berlangsung, mohon bersabar.", Toast.LENGTH_SHORT).show();
                } else {
                    if (mImageUri != null){
                        mStorageRef = FirebaseStorage.getInstance().getReference("bukti_pembayaran").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tanggalPesanUser).child("id_pesanan").child(idPesanan);
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("pesanan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tanggalPesanUser).child("id_pesanan").child(idPesanan).child("bukti_pembayaran");
                        ref = FirebaseDatabase.getInstance().getReference("pesanan_pemilik").child(UIDMItra).child(tanggalPesanan).child("id_pesanan").child(idPesanan).child("bukti_pembayaran");
                        final DatabaseReference statusRef1 = FirebaseDatabase.getInstance().getReference("pesanan_pemilik").child(UIDMItra).child(tanggalPesanan).child("id_pesanan").child(idPesanan).child("status_pesanan");
                        final DatabaseReference statusRef2 = FirebaseDatabase.getInstance().getReference("pesanan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tanggalPesanUser).child("id_pesanan").child(idPesanan).child("status_pesanan");
                        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
                        mUploadTask = fileReference.putFile(mImageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setProgress(0);
                                            }
                                        }, 2000);
                                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                bukti_pembayaran = uri.toString();
                                                mDatabaseRef.setValue(bukti_pembayaran);
                                                ref.setValue(bukti_pembayaran);
                                                statusRef1.setValue(statusPembayaran);
                                                statusRef2.setValue(statusPembayaran);
                                            }
                                        });
                                        total_pesanan++;
                                        totalPesananRef.setValue(total_pesanan);
                                        setAvailablityLapangan();
                                        PesananData pesananData = new PesananData(idPesanan, namaPemesan, total_harga, bukti_pembayaran,
                                                jam_pesan, tanggalPesanan, statusPembayaran, nama_lapangan, alasan_status, UIDMItra, UIDPelanggan, tanggalPesanUser, idLapangan);
                                        DatabaseReference pesananMitra = FirebaseDatabase.getInstance().getReference("pesanan_pemilik")
                                                .child(UIDMItra).child(tanggalPesanan).child("id_pesanan").child(idPesanan);
                                        pesananMitra.setValue(pesananData);
                                        Toast.makeText(UploadBuktiPembayaranActivity.this, "Upload Bukti Pembayaran Berhasil!", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(UploadBuktiPembayaranActivity.this, BerhasilUploadActivity.class);
                                        intent1.putExtra(ID_PESANAN, idPesanan);
                                        startActivity(intent1);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UploadBuktiPembayaranActivity.this, "Ups ada kesalahan : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressBar.setProgress((int) progress);
                                    }
                                });
                    } else {
                        Toast.makeText(UploadBuktiPembayaranActivity.this, "Anda Belum Memilih Gambar", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void setAvailablityLapangan() {
        DatabaseReference ketersediaanRef;
        ketersediaanRef = FirebaseDatabase.getInstance().getReference("ketersediaan_lapangan").child(idLapangan).child(tanggalPesanan);
        String[] splitString = jam_pesan.split(", ");
        List<String> jamArrayList = new ArrayList<>();
        jamArrayList = Arrays.asList(splitString);
        for (String s : jamArrayList){
            ketersediaanRef.child(s).setValue(namaPemesan);
        }
    }

    private void readDataFirebase() {
        totalPesananRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    total_pesanan = Integer.parseInt(snapshot.getValue().toString());
                } else {
                    total_pesanan = 0;
                    totalPesananRef.setValue(total_pesanan);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        totalPesananRef.keepSynced(true);
    }

    // milih gambar
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Glide.with(this).load(mImageUri).into(gambarBukti);
            gambarBukti.setVisibility(View.VISIBLE);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
