package com.gelora.pengguna.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gelora.pengguna.BuildConfig;
import com.gelora.pengguna.R;
import com.gelora.pengguna.adapter.JamSewaAdapter;
import com.gelora.pengguna.interfaces.OnJamClickListener;
import com.gelora.pengguna.model.PesananData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midtrans.sdk.corekit.callback.TransactionCallback;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.SdkCoreFlowBuilder;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.gelora.pengguna.adapter.LapanganAdapter.GAMBAR_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.HARGA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.ID_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.JENIS_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.KATEGORI_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.NAMA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.UID_MITRA;

public class PesanLapanganActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, OnJamClickListener, TransactionFinishedCallback {

    public static final String ID_PESANAN = "com.gelora.pengguna.id_pesanan";
    public static final String NAMA_PEMESAN = "com.gelora.pengguna.nama_pemesan";
    public static final String TOTAL_HARGA = "com.gelora.pengguna.total_harga";
    public static final String BUKTI_PEMBAYARAN = "com.gelora.pengguna.bukti_pembayaran";
    public static final String JAM_PESANAN = "com.gelora.pengguna.jam_pesanan";
    public static final String TANGGAL_PESANAN = "com.gelora.pengguna.tanggal_pesanan";
    public static final String STATUS_PESANAN = "com.gelora.pengguna.status_pesanan";
    public static final String ALASAN_PESANAN = "com.gelora.pengguna.alasan_pesanan";
    public static final String UID_PELANGGAN = "com.gelora.pengguna.uid_pelanggan";
    public static final String TANGGAL_PESAN_USER = "com.gelora.pengguna.tanggal_pesan_user";
    public static final String FAVORIT_LAPANGAN = "com.gelora.pengguna.favorit_lapangan";


    TextView namaLapangan, kategoriLapangan, jenisLapangan, hargaLapangan, pilihTanggalLapangan, tanggalLapanganReview, jamLapanganReview, hargaLapanganReview;
    ImageView gambharLapangan, backButton, favoritButton;
    DatabaseReference ref, ketersedianLapanganRef, userNameRef, pesananRef, pemilikLpaanganRef, favRef;
    Button datePicker, bayarButton;
    String idLapanganIntent, namaLapanganIntent, gambarLapanganIntent, kategoriLapanganIntent, jenisLapanganIntent, UIDMitraIntent;
    public static String namaPemesan;
    long totalHarga;
    long hargaLapanganIntent;
    ArrayList<String> jamArrayList;
    ArrayList<String> jamSewaArrayList;
    RecyclerView jamSewaRecycler;
    String textA;
    Locale locale = new Locale("id", "ID");
    NumberFormat n = NumberFormat.getCurrencyInstance(locale);
    int idPesanan = 0;
    int panjangArrayListJam;
    int price;
    String bukti_pembayaran = "belum ada";
    String status_pesanan = "Belum Upload Bukti";
    private static final String TAG = "PesanLapanganActivity";
    String alasan_pesanan = "Tidak Ada";
    ProgressBar progressBar;
    String uid_pelanggan;
    String tanggalHariIni;
    Boolean isFavorited = false;

    @Override
    protected void onStart() {
        super.onStart();

    }

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
        bayarButton = findViewById(R.id.bayar_button);
        progressBar = findViewById(R.id.progressbarlingkaran);
        favoritButton = findViewById(R.id.favoritLapanganButton);

        // ambil nama pemain
        userNameRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("nama");
        userNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                namaPemesan = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // id pesanan counter
        pesananRef = FirebaseDatabase.getInstance().getReference("pesanan");
        pesananRef.child("pesanan_counter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    idPesanan = Integer.parseInt(snapshot.getValue().toString());
                } else {
                    pesananRef.child("pesanan_counter").setValue(idPesanan);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //inisiasi MidtransSDK
        iniMidtransSDK();

        // ambil tanggal hari ini
        hariniTanggalBerapa();

        // ambil data dari Intent sebelumnya
        final Intent intent = getIntent();
        idLapanganIntent = intent.getStringExtra(ID_LAPANGAN);
        namaLapanganIntent = intent.getStringExtra(NAMA_LAPANGAN);
        gambarLapanganIntent = intent.getStringExtra(GAMBAR_LAPANGAN);
        kategoriLapanganIntent = intent.getStringExtra(KATEGORI_LAPANGAN);
        jenisLapanganIntent = intent.getStringExtra(JENIS_LAPANGAN);
        hargaLapanganIntent = intent.getLongExtra(HARGA_LAPANGAN, 0);
        UIDMitraIntent = intent.getStringExtra(UID_MITRA);
        isFavorited = intent.getBooleanExtra(FAVORIT_LAPANGAN, false);

        //
        jamSewaRecycler.setHasFixedSize(true);
        jamSewaRecycler.setLayoutManager(new LinearLayoutManager(this));
        // set data dari intent
        Glide.with(this)
                .load(gambarLapanganIntent)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(gambharLapangan);
        namaLapangan.setText(namaLapanganIntent);
        kategoriLapangan.setText(kategoriLapanganIntent);
        jenisLapangan.setText(jenisLapanganIntent);
        tanggalLapanganReview.setText("");
        String hargaText = n.format(hargaLapanganIntent);
        String hargaSetText = hargaText.replaceAll(",00", "").replaceAll("Rp", "Rp. ");
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

        // cek kondisi review section
        bayarButton.setOnClickListener(new View.OnClickListener() {
            String name = "Sewa " + namaLapanganIntent;

            @Override
            public void onClick(View v) {
                if (tanggalLapanganReview.getText().toString().equals("")){
                    Toast.makeText(PesanLapanganActivity.this, "Silakan pilih tanggal terlebih dahulu", Toast.LENGTH_SHORT).show();
                    return;
                } else if (jamLapanganReview.getText().toString().equals("")){
                    Toast.makeText(PesanLapanganActivity.this, "Silakan pilih jam bermain terlebih dahulu", Toast.LENGTH_SHORT).show();
                    return;
                }
                MidtransSDK.getInstance().setTransactionRequest(transactionRequest(String.valueOf(idPesanan), price, 1, name));
                MidtransSDK.getInstance().startPaymentUiFlow(PesanLapanganActivity.this);
            }
        });


        favRef = FirebaseDatabase.getInstance().getReference("favorit_lapangan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(idLapanganIntent);
        readFav(new FirebaseCallback() {
            @Override
            public void favorit(Boolean favBool) {
                isFavorited = favBool;
                if (isFavorited == true){
                    Glide.with(getApplicationContext())
                            .load(R.drawable.ic_favorit_solid)
                            .centerCrop()
                            .override(80)
                            .into(favoritButton);
                    favoritButton.setColorFilter(Color.RED);
                } else if (isFavorited == false){
                    Glide.with(getApplicationContext())
                            .load(R.drawable.ic_fav_border_only)
                            .centerCrop()
                            .override(80)
                            .into(favoritButton);
                    favoritButton.setColorFilter(Color.BLACK);
                }
            }
        });

        // favorite
        favoritButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorited == false){
                    Glide.with(getApplicationContext())
                            .load(R.drawable.ic_favorit_solid)
                            .into(favoritButton);
                    isFavorited = true;
                    favoritButton.setColorFilter(Color.RED);
                    favRef.setValue(namaLapanganIntent);
                    Toast.makeText(PesanLapanganActivity.this, "Favorit "+namaLapanganIntent+" berhasil!", Toast.LENGTH_SHORT).show();
                } else {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.ic_fav_border_only)
                            .into(favoritButton);
                    isFavorited = false;
                    favoritButton.setColorFilter(Color.BLACK);
                    favRef.removeValue();
                    Toast.makeText(PesanLapanganActivity.this, "Batalkan Favorit "+namaLapanganIntent, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void readFav(final FirebaseCallback firebaseCallback){
        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    firebaseCallback.favorit(true);
                } else {
                    firebaseCallback.favorit(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private interface FirebaseCallback{
        void favorit(Boolean favBool);
    }
    private void hariniTanggalBerapa() {
        Calendar c = Calendar.getInstance();
        tanggalHariIni = DateFormat.getDateInstance(DateFormat.FULL, locale).format(c.getTime());
        System.out.println("Tanggal hari ini : "+tanggalHariIni);
    }

    private void iniMidtransSDK() {
        SdkUIFlowBuilder.init()
                .setContext(this)
                .setMerchantBaseUrl(BuildConfig.MERCHANT_BASE_URL)
                .setClientKey(BuildConfig.MERCHANT_CLIENT_KEY)
                .setTransactionFinishedCallback(this)
                .enableLog(true)
                .setColorTheme(new CustomColorTheme("#34A853", "#34A853", "#FFE51255"))
                .buildSDK();
        UIKitCustomSetting uiKitCustomSetting = new UIKitCustomSetting();
        uiKitCustomSetting.setSkipCustomerDetailsPages(true);
        MidtransSDK.getInstance().setUIKitCustomSetting(uiKitCustomSetting);
    }

    public static TransactionRequest transactionRequest(String id, int price, int qty, String name) {
        TransactionRequest request = new TransactionRequest(System.currentTimeMillis() + "", price);
        CustomerDetails cd = new CustomerDetails();
        cd.setFirstName(namaPemesan);
        request.setCustomerDetails(cd);
        ItemDetails details = new ItemDetails(id, price, qty, name);
        ArrayList<ItemDetails> itemDetails = new ArrayList<>();
        itemDetails.add(details);
        request.setItemDetails(itemDetails);
        return request;
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
                if (!snapshot.exists()) {
                    for (int i = 0; i < jamArrayList.size(); i++) {
                        ketersedianLapanganRef.child(jamArrayList.get(i)).setValue("tersedia");
                    }
                    System.out.println(jamArrayList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PesanLapanganActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    jamSewaRecycler.setLayoutManager(linearLayoutManager);
                    JamSewaAdapter jamSewaAdapter = new JamSewaAdapter(PesanLapanganActivity.this, jamArrayList, PesanLapanganActivity.this);
                    jamSewaRecycler.setAdapter(jamSewaAdapter);
                } else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getValue().equals("tersedia")) {
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
        if (jamList.isEmpty()) {
            textA = "";

        } else {
            for (int i = 0; i < jamList.size(); i++) {
                textA = jamList.toString();
            }
        }
        String str = textA.replaceAll("\\[", "").replaceAll("\\]", "");
        jamLapanganReview.setText(str);
        int multiplier = jamList.size();
        long harga = hargaLapanganIntent;
        totalHarga = harga * multiplier;
        panjangArrayListJam = jamList.size();
        String s = n.format(totalHarga);
        String a = s.replaceAll(",00", "").replaceAll("Rp", "Rp. ");
        System.out.println(a);
        hargaLapanganReview.setText(a);
        price = (int) totalHarga;
    }

    @Override
    public void onTransactionFinished(TransactionResult transactionResult) {
        if (transactionResult.getResponse() != null) {
            switch (transactionResult.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaction Finished ID : " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
                    passData();
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction Pending ID : " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
                    passData();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction Failed ID : " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
                    break;
            }
            transactionResult.getResponse().getValidationMessages();
        } else if (transactionResult.isTransactionCanceled()) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_SHORT).show();
        } else {
            if (transactionResult.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Transaction Finished with failure", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void passData(){
        uid_pelanggan = FirebaseAuth.getInstance().getCurrentUser().getUid();
        idPesanan++;
        pesananRef.child("pesanan_counter").setValue(idPesanan);
        String jamlapanganText = jamLapanganReview.getText().toString();
        String tanggalLapanganText = tanggalLapanganReview.getText().toString();
        PesananData pesananData = new PesananData(String.valueOf(idPesanan), namaPemesan, price, bukti_pembayaran, jamlapanganText, tanggalLapanganText, status_pesanan, namaLapanganIntent, alasan_pesanan,UIDMitraIntent, uid_pelanggan, tanggalHariIni, idLapanganIntent);
        pesananRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tanggalHariIni).child("id_pesanan").child(String.valueOf(idPesanan)).setValue(pesananData);
        Log.d(TAG, "passData: Passing Data to Firebase");
        Intent intent = new Intent(PesanLapanganActivity.this, UploadBuktiPembayaranActivity.class);
        intent.putExtra(ID_PESANAN, String.valueOf(idPesanan));
        intent.putExtra(NAMA_PEMESAN, namaPemesan);
        intent.putExtra(TOTAL_HARGA, price);
        intent.putExtra(BUKTI_PEMBAYARAN, bukti_pembayaran);
        intent.putExtra(JAM_PESANAN, jamlapanganText);
        intent.putExtra(TANGGAL_PESANAN, tanggalLapanganText);
        intent.putExtra(STATUS_PESANAN, status_pesanan);
        intent.putExtra(NAMA_LAPANGAN, namaLapanganIntent);
        intent.putExtra(ALASAN_PESANAN, alasan_pesanan);
        intent.putExtra(UID_MITRA, UIDMitraIntent);
        intent.putExtra(UID_PELANGGAN, uid_pelanggan);
        intent.putExtra(TANGGAL_PESAN_USER, tanggalHariIni);
        intent.putExtra(ID_LAPANGAN, idLapanganIntent);
        startActivity(intent);
        finish();
    }
}
