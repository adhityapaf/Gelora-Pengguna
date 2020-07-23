package com.gelora.pengguna.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gelora.pengguna.R;
import com.gelora.pengguna.model.PesananData;

import java.util.ArrayList;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.ViewHolder> {

    ArrayList<PesananData> pesananData;
    Context mContext;

    public PesananAdapter(ArrayList<PesananData> pesananData, Context mContext) {
        this.pesananData = pesananData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PesananAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesananAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
