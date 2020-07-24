package com.gelora.pengguna.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gelora.pengguna.R;
import com.gelora.pengguna.adapter.PesananAdapter;
import com.gelora.pengguna.model.PesananData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {
    RecyclerView recyclerView;
    Context mContext;
    DatabaseReference orderRef;
    ArrayList<String> list;
    ProgressBar progressBar;
    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        recyclerView = view.findViewById(R.id.orderRecycler);
        progressBar = view.findViewById(R.id.progressbarlingkaran);
        progressBar.setVisibility(View.VISIBLE);
        mContext = getContext();
        orderRef = FirebaseDatabase.getInstance().getReference("pesanan").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        readData();
        return view;
    }

    private void readData() {
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list = new ArrayList<>();
                    for (DataSnapshot ds  : snapshot.getChildren()){
                        list.add(ds.getKey());
                    }
                    PesananAdapter pesananAdapter = new PesananAdapter(list, getActivity());
                    recyclerView.setAdapter(pesananAdapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(mContext, "Pesananmu kosong, yuk pesan sekarang!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        orderRef.keepSynced(true);
    }
}
