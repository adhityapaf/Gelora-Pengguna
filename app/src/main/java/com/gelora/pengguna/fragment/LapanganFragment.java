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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    final static String DATA_RECEIVE = "data_receive";

    ImageView backButton;
    TextView namaKategori;
    RecyclerView lapanganRecycler;
    Context mContext;
    DatabaseReference ref;
    ArrayList<LapanganData> list;
    ArrayList<LapanganData> queryList;
    ProgressBar progressBar;
    public LapanganFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lapangan, container, false);
        lapanganRecycler = view.findViewById(R.id.lapanganRecyler);
        progressBar = view.findViewById(R.id.progressbarlingkaran);
        progressBar.setVisibility(View.VISIBLE);
        mContext = getContext();
        backButton = view.findViewById(R.id.back_arrow);
        namaKategori = view.findViewById(R.id.namaKategori);
        backButton.setVisibility(View.GONE);
        namaKategori.setVisibility(View.GONE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit();
            }
        });
        ref = FirebaseDatabase.getInstance().getReference("lapangan").child("id_lapangan");
        lapanganRecycler.setHasFixedSize(true);
        lapanganRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        readData();
        return view;
    }

    void readData(){
        ref.addValueEventListener(new ValueEventListener() {
            Bundle bundle = getArguments();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        if (bundle != null){
                            if (ds.child("kategori_lapangan").getValue().equals(bundle.getString(DATA_RECEIVE))){
                                LapanganData lp = ds.getValue(LapanganData.class);
                                list.add(lp);
                            }
                            backButton.setVisibility(View.VISIBLE);
                            namaKategori.setVisibility(View.VISIBLE);
                            namaKategori.setText(bundle.getString(DATA_RECEIVE));
                        } else {
                            LapanganData lp = ds.getValue(LapanganData.class);
                            list.add(lp);
                        }
                    }
                    LapanganAdapter lapanganAdapter = new LapanganAdapter(list, getActivity());
                    lapanganRecycler.setAdapter(lapanganAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
