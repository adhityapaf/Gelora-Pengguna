package com.gelora.pengguna.fragment;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gelora.pengguna.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    Bundle bundle = new Bundle();
    String kategori = "";
    CardView futsal, sepakBola, basket, buluTangkis;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_home, container, false);
        futsal = view.findViewById(R.id.futsal);
        sepakBola = view.findViewById(R.id.sepak_bola);
        basket = view.findViewById(R.id.basket);
        buluTangkis = view.findViewById(R.id.bulu_tangkis);

        futsal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategori = "Futsal";
                bundle.putString(LapanganFragment.DATA_RECEIVE, kategori);
                LapanganFragment lapanganFragment = new LapanganFragment();
                lapanganFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, lapanganFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        sepakBola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategori = "Sepak Bola";
                bundle.putString(LapanganFragment.DATA_RECEIVE, kategori);
                LapanganFragment lapanganFragment = new LapanganFragment();
                lapanganFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, lapanganFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategori = "Basket";
                bundle.putString(LapanganFragment.DATA_RECEIVE, kategori);
                LapanganFragment lapanganFragment = new LapanganFragment();
                lapanganFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, lapanganFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        buluTangkis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategori = "Bulu Tangkis";
                bundle.putString(LapanganFragment.DATA_RECEIVE, kategori);
                LapanganFragment lapanganFragment = new LapanganFragment();
                lapanganFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, lapanganFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}
