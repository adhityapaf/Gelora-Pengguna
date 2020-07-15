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

import com.gelora.pengguna.R;
import com.gelora.pengguna.adapter.LapanganAdapter;
import com.gelora.pengguna.model.LapanganData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LapanganFragment extends Fragment {

    RecyclerView lapanganRecycler;
    Context mContext;
    DatabaseReference ref;
    ArrayList<LapanganData> list;
    public LapanganFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lapangan, container, false);
        lapanganRecycler = view.findViewById(R.id.lapanganRecyler);
        mContext = getContext();
        ref = FirebaseDatabase.getInstance().getReference("lapangan").child("id_lapangan");
        lapanganRecycler.setHasFixedSize(true);
        lapanganRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        readData();
        return view;
    }

    void readData(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        LapanganData lp = ds.getValue(LapanganData.class);
                        list.add(lp);
                    }
                    LapanganAdapter lapanganAdapter = new LapanganAdapter(list, getActivity());
                    lapanganRecycler.setAdapter(lapanganAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
