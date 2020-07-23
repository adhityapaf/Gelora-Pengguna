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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import static com.gelora.pengguna.activity.PesanLapanganActivity.ID_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_PESANAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.UID_MITRA;

public class UploadBuktiPembayaranActivity extends AppCompatActivity {
    private static final String TAG = "UploadBuktiPembayaranAc";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    ImageView gambarBukti, backButton;
    Button pilihGambarButton, uploadButton;
    ProgressBar progressBar;
    DatabaseReference ref;
    String idPesanan, UIDMItra, tanggalPesanan;
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
        UIDMItra = intent.getStringExtra(UID_MITRA);
        tanggalPesanan = intent.getStringExtra(TANGGAL_PESANAN);

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
                        mStorageRef = FirebaseStorage.getInstance().getReference("bukti_pembayaran").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tanggalPesanan).child("id_pesanan").child(idPesanan);
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("pesanan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tanggalPesanan).child("id_pesanan").child(idPesanan).child("bukti_pembayaran");
                        ref = FirebaseDatabase.getInstance().getReference("pesanan_pemilik").child(UIDMItra).child("id_pesanan").child(idPesanan).child("bukti_pembayaran");
                        final DatabaseReference statusRef1 = FirebaseDatabase.getInstance().getReference("pesanan_pemilik").child(UIDMItra).child("id_pesanan").child(idPesanan).child("status_pesanan");
                        final DatabaseReference statusRef2 = FirebaseDatabase.getInstance().getReference("pesanan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tanggalPesanan).child("id_pesanan").child(idPesanan).child("status_pesanan");
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
                                                String downloadUrl = uri.toString();
                                                mDatabaseRef.setValue(downloadUrl);
                                                ref.setValue(downloadUrl);
                                                statusRef1.setValue("Sudah Upload Bukti");
                                                statusRef2.setValue("Sudah Upload Bukti");
                                            }
                                        });
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
