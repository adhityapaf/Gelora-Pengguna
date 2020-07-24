package com.gelora.pengguna.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gelora.pengguna.R;
import com.gelora.pengguna.model.PesananData;

import java.util.ArrayList;

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
    public void onBindViewHolder(@NonNull PesananListAdapter.ViewHolder holder, int position) {
        holder.orderId.setText(pesananData.get(position).getId_pesanan());
        holder.orderStatus.setText(pesananData.get(position).getStatus_pesanan());
        holder.lihatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Detail Clicked!", Toast.LENGTH_SHORT).show();
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
