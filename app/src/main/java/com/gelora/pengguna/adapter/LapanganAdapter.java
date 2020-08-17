package com.gelora.pengguna.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
    public static final String UID_MITRA = "com.gelora.pengguna.uid_mitra";
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
        holder.namaLapangan.setText(lapanganData.get(position).getNama_lapangan());
        holder.kategoriLapangan.setText(lapanganData.get(position).getKategori_lapangan());
        holder.jenisLapangan.setText(lapanganData.get(position).getJenis_lapangan());
        holder.hargaLapangan.setText("Rp. "+lapanganData.get(position).getHarga());
        holder.lapanganCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PesanLapanganActivity.class);
                String idLapanganStr, namaLapanganStr, kategoriLapanganStr, jenisLapanganStr, gambarLapanganStr;
                gambarLapanganStr = lapanganData.get(position).getGambar_lapangan();
                namaLapanganStr = holder.namaLapangan.getText().toString();
                kategoriLapanganStr = holder.kategoriLapangan.getText().toString();
                jenisLapanganStr = holder.jenisLapangan.getText().toString();
                long hargaLapanganStr = lapanganData.get(position).getHarga();
                idLapanganStr = lapanganData.get(position).getId_lapangan();
                intent.putExtra(ID_LAPANGAN, idLapanganStr);
                intent.putExtra(NAMA_LAPANGAN, namaLapanganStr);
                intent.putExtra(GAMBAR_LAPANGAN, gambarLapanganStr);
                intent.putExtra(KATEGORI_LAPANGAN, kategoriLapanganStr);
                intent.putExtra(JENIS_LAPANGAN, jenisLapanganStr);
                intent.putExtra(HARGA_LAPANGAN, hargaLapanganStr);
                intent.putExtra(UID_MITRA, lapanganData.get(position).getUID_Mitra());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lapanganData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView gambarLapangan;
        public TextView namaLapangan;
        public TextView kategoriLapangan;
        public TextView jenisLapangan;
        public TextView hargaLapangan;
        public CardView lapanganCard;
        public ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gambarLapangan = itemView.findViewById(R.id.img_lapangan);
            namaLapangan = itemView.findViewById(R.id.nama_lapangan);
            kategoriLapangan = itemView.findViewById(R.id.kategori_lapangan);
            jenisLapangan = itemView.findViewById(R.id.jenis_rumput);
            hargaLapangan = itemView.findViewById(R.id.harga_sewa);
            lapanganCard = itemView.findViewById(R.id.lapanganCard);
            progressBar = itemView.findViewById(R.id.progressbarlingkaran);
        }
    }
}
