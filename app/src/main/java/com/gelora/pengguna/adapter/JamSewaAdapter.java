package com.gelora.pengguna.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gelora.pengguna.R;
import com.gelora.pengguna.activity.PesanLapanganActivity;
import com.gelora.pengguna.interfaces.OnJamClickListener;

import java.util.ArrayList;
import java.util.HashMap;

public class JamSewaAdapter extends RecyclerView.Adapter<JamSewaAdapter.ViewHolder> {


    Context mContext;
    ArrayList<String> jamArrayList = new ArrayList<>();
    ArrayList<String> pilihan;
    OnJamClickListener onJamClickListener;

    public JamSewaAdapter(Context mContext, ArrayList<String> jamArrayList, OnJamClickListener onJamClickListener) {
        this.mContext = mContext;
        this.jamArrayList = jamArrayList;
        this.onJamClickListener = onJamClickListener;
    }

    @NonNull
    @Override
    public JamSewaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.jam_lapangan_items, parent, false);
        pilihan = new ArrayList<>();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final JamSewaAdapter.ViewHolder holder, int position) {
        holder.jamText.setText(jamArrayList.get(position));
        holder.jamRelative.setOnClickListener(new View.OnClickListener() {
            Boolean buttonClicked = false;
            @Override
            public void onClick(View v) {
                if (pilihan.size() >= 3){
                    Toast.makeText(mContext, "Maksimal Sewa 3 Jam", Toast.LENGTH_SHORT).show();
                    buttonClicked = true;
                }
                if (buttonClicked == false) {
                    holder.jamRelative.setBackground(mContext.getResources().getDrawable(R.drawable.button_green_rounded_5dp));
                    holder.jamText.setTextColor(Color.WHITE);
                    buttonClicked = true;
                    pilihan.add(holder.jamText.getText().toString());
                } else {
                    holder.jamRelative.setBackground(mContext.getResources().getDrawable(R.drawable.green_stroke_rectangle));
                    holder.jamText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    buttonClicked = false;
                    pilihan.remove(holder.jamText.getText().toString());
                }

                System.out.println(pilihan);
                onJamClickListener.jamReview(pilihan);
            }
        });

    }

    @Override
    public int getItemCount() {
        return jamArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout jamRelative;
        TextView jamText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jamRelative = itemView.findViewById(R.id.jam_items_relative);
            jamText = itemView.findViewById(R.id.jam_lapangan_isi);
        }
    }
}
