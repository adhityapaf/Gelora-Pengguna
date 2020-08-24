package com.gelora.pengguna.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gelora.pengguna.R;
import com.gelora.pengguna.activity.DetailPesananActivity;
import com.gelora.pengguna.model.PesananData;

import java.util.ArrayList;

import static com.gelora.pengguna.activity.PesanLapanganActivity.ALASAN_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.BUKTI_PEMBAYARAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.ID_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.JAM_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.NAMA_PEMESAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.STATUS_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_LAPANGAN_MILLIS;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_PESANAN;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_PESAN_USER;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TANGGAL_PESAN_USER_MILLIS;
import static com.gelora.pengguna.activity.PesanLapanganActivity.TOTAL_HARGA;
import static com.gelora.pengguna.adapter.LapanganAdapter.ID_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.NAMA_LAPANGAN;
import static com.gelora.pengguna.adapter.LapanganAdapter.UID_MITRA;

public class PesananListAdapter extends RecyclerView.Adapter<PesananListAdapter.ViewHolder> {

    ArrayList<PesananData> pesananData;
    Context mContext;

    public PesananListAdapter(ArrayList<PesananData> pesananData, Context mContext) {
        this.pesananData = pesananData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PesananListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_list_items, parent, false);
        return new PesananListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PesananListAdapter.ViewHolder holder, final int position) {
        holder.orderId.setText(pesananData.get(position).getId_pesanan());
        if (pesananData.get(position).getStatus_pesanan().equals("Belum Upload Bukti")){
            holder.orderStatus.setText("Menunggu Pembayaran");
            holder.orderStatus.setTextSize(14);
        }
        if (pesananData.get(position).getStatus_pesanan().equals("Sudah Upload Bukti")){
            holder.orderStatus.setText("Dibayar");
            holder.orderStatus.setTextColor(Color.BLUE);
        }
        if (pesananData.get(position).getStatus_pesanan().equals("Diterima")){
            holder.orderStatus.setText("Diterima");
            holder.orderStatus.setTextColor(Color.parseColor("#34A853"));
        }
        if (pesananData.get(position).getStatus_pesanan().equals("Ditolak")){
            holder.orderStatus.setText("Ditolak");
            holder.orderStatus.setTextColor(Color.RED);
        }
        holder.lihatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailPesananActivity.class);
                intent.putExtra(ID_PESANAN, holder.orderId.getText().toString());
                intent.putExtra(NAMA_PEMESAN, pesananData.get(position).getNama_pemesan());
                intent.putExtra(TOTAL_HARGA, pesananData.get(position).getTotal_harga());
                intent.putExtra(BUKTI_PEMBAYARAN, pesananData.get(position).getBukti_pembayaran());
                intent.putExtra(JAM_PESANAN, pesananData.get(position).getJam_pesan());
                intent.putExtra(TANGGAL_PESANAN, pesananData.get(position).getTanggal_pesan());
                intent.putExtra(STATUS_PESANAN, pesananData.get(position).getStatus_pesanan());
                intent.putExtra(NAMA_LAPANGAN, pesananData.get(position).getNama_lapangan());
                intent.putExtra(ALASAN_PESANAN, pesananData.get(position).getAlasan_status());
                intent.putExtra(UID_MITRA, pesananData.get(position).getUid_mitra());
                intent.putExtra(TANGGAL_PESAN_USER, pesananData.get(position).getTanggal_pesan_user());
                intent.putExtra(ID_LAPANGAN, pesananData.get(position).getId_lapangan());
                intent.putExtra(TANGGAL_LAPANGAN_MILLIS, pesananData.get(position).getTanggalLapanganMillis());
                intent.putExtra(TANGGAL_PESAN_USER_MILLIS, pesananData.get(position).getTanggalPesanUserMillis());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pesananData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderStatus, lihatDetail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            lihatDetail = itemView.findViewById(R.id.lihatBukti);
        }
    }
}
