package com.gelora.pengguna.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gelora.pengguna.R;
import com.gelora.pengguna.activity.PesanLapanganActivity;
import com.gelora.pengguna.model.LapanganData;

import java.util.ArrayList;
import java.util.HashMap;

public class LapanganAdapter extends RecyclerView.Adapter<LapanganAdapter.ViewHolder>{

    ArrayList<LapanganData> lapanganData;
    Context mContext;
    private static final String TAG = "LapanganAdapter";
    public static final String ID_LAPANGAN = "com.gelora.pengguna.id_lapangan";
    public static final String NAMA_LAPANGAN = "com.gelora.pengguna.nama_lapangan";
    public static final String KATEGORI_LAPANGAN = "com.gelora.pengguna.kategori_lapangan";
    public static final String JENIS_LAPANGAN = "com.gelora.pengguna.jenis_lapangan";
    public static final String HARGA_LAPANGAN = "com.gelora.pengguna.harga_lapangan";
    public static final String GAMBAR_LAPANGAN = "com.gelora.pengguna.gambar_lapangan";
    public LapanganAdapter(ArrayList<LapanganData> lapanganData, Context mContext) {
        this.lapanganData = lapanganData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public LapanganAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lapangan_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LapanganAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(lapanganData.get(position).getGambar_lapangan())
                .centerCrop()
                .placeholder(R.drawable.img_placeholder_lapangan)
                .into(holder.gambarLapangan);
        holder.namaLapangan.setText(lapanganData.get(position).getNama_lapangan());
        holder.kategoriLapangan.setText(lapanganData.get(position).getKategori_lapangan());
        holder.jenisLapangan.setText(lapanganData.get(position).getJenis_lapangan());
        holder.hargaLapangan.setText("Rp. "+lapanganData.get(position).getHarga());
        holder.lapanganCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PesanLapanganActivity.class);
                String idLapanganStr, namaLapanganStr, kategoriLapanganStr, jenisLapanganStr, hargaLapanganStr, gambarLapanganStr;
                gambarLapanganStr = lapanganData.get(position).getGambar_lapangan();
                namaLapanganStr = holder.namaLapangan.getText().toString();
                kategoriLapanganStr = holder.kategoriLapangan.getText().toString();
                jenisLapanganStr = holder.jenisLapangan.getText().toString();
                hargaLapanganStr = holder.hargaLapangan.getText().toString();
                idLapanganStr = lapanganData.get(position).getId_lapangan();
                intent.putExtra(ID_LAPANGAN, idLapanganStr);
                intent.putExtra(NAMA_LAPANGAN, namaLapanganStr);
                intent.putExtra(GAMBAR_LAPANGAN, gambarLapanganStr);
                intent.putExtra(KATEGORI_LAPANGAN, kategoriLapanganStr);
                intent.putExtra(JENIS_LAPANGAN, jenisLapanganStr);
                intent.putExtra(HARGA_LAPANGAN, hargaLapanganStr);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lapanganData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gambarLapangan;
        TextView namaLapangan, kategoriLapangan, jenisLapangan, hargaLapangan;
        CardView lapanganCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gambarLapangan = itemView.findViewById(R.id.img_lapangan);
            namaLapangan = itemView.findViewById(R.id.nama_lapangan);
            kategoriLapangan = itemView.findViewById(R.id.kategori_lapangan);
            jenisLapangan = itemView.findViewById(R.id.jenis_rumput);
            hargaLapangan = itemView.findViewById(R.id.harga_sewa);
            lapanganCard = itemView.findViewById(R.id.lapanganCard);

        }
    }
}
