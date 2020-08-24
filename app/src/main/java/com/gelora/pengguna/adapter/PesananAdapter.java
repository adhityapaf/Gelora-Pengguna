package com.gelora.pengguna.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gelora.pengguna.R;
import com.gelora.pengguna.model.PesananData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.ViewHolder> {
    private static final String TAG = "PesananAdapter";
    ArrayList<String> pesananData;
    ArrayList<PesananData> listPesananData;
    Context mContext;
    String tanggalpesanan;
    DatabaseReference ref;
    Locale locale = new Locale("id", "ID");
    Long tanggalMillis;

    public PesananAdapter(ArrayList<String> pesananData, Context mContext) {
        this.pesananData = pesananData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PesananAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_items, parent, false);
        ref = FirebaseDatabase.getInstance().getReference("pesanan").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PesananAdapter.ViewHolder holder, int position) {
        DateFormat format = new SimpleDateFormat("EEEEEEE, dd MMMM yyyy", locale);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(pesananData.get(position)));
        holder.tanggalPesanan.setText(format.format(calendar.getTime()));
        holder.pesananListRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true);
        holder.pesananListRecycler.setLayoutManager(linearLayoutManager);
        ref.child(pesananData.get(position)).child("id_pesanan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    listPesananData = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        PesananData pesananData = ds.getValue(PesananData.class);
                        listPesananData.add(pesananData);
                    }
                    PesananListAdapter pesananListAdapter = new PesananListAdapter(listPesananData, mContext);
                    holder.pesananListRecycler.setAdapter(pesananListAdapter);
                } else  {
                    Log.d(TAG, "TanggalPesanan List : Not Available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child(pesananData.get(position)).child("id_pesanan").keepSynced(true);
    }

    @Override
    public int getItemCount() {
        return pesananData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tanggalPesanan;
        RecyclerView pesananListRecycler;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tanggalPesanan = itemView.findViewById(R.id.tanggal_transaksi);
            pesananListRecycler = itemView.findViewById(R.id.orderListRecycler);
        }
    }
}
