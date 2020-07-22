package com.gelora.pengguna.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gelora.pengguna.R;

import static com.gelora.pengguna.activity.PesanLapanganActivity.ID_PESANAN;

public class BerhasilUploadActivity extends AppCompatActivity {
    TextView orderID;
    Button backToHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berhasil_upload);
        orderID = findViewById(R.id.orderIdLabel);
        backToHome = findViewById(R.id.backToHomeButton);

        // ambil data dari intent sebelumnya
        Intent intent = getIntent();
        orderID.setText("Order ID : "+intent.getStringExtra(ID_PESANAN));

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
