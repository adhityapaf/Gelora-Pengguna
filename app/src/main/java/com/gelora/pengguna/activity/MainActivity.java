package com.gelora.pengguna.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.gelora.pengguna.R;
import com.gelora.pengguna.fragment.FavoriteFragment;
import com.gelora.pengguna.fragment.HomeFragment;
import com.gelora.pengguna.fragment.OrderFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar bottomNav;
    FragmentManager  fragmentManager;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState==null){
            bottomNav.setItemSelected(R.id.home, true);
            fragmentManager =getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        }
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.favorite:
                        fragment = new FavoriteFragment();
                        break;
                    case R.id.list:
                        fragment = new OrderFragment();
                        break;
                }
                if (fragment!=null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                } else {
                    Log.d(TAG, "onItemSelected: Error in Creating Fragment");
                }
            }
        });
    }
}
