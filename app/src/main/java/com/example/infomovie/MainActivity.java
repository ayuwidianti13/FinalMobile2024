package com.example.infomovie;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.infomovie.fragment.FavoriteFragment;
import com.example.infomovie.fragment.HomeFragment;
import com.example.infomovie.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Pastikan untuk mengatur layout activity_main

        // Mendapatkan username dari LoginActivity
        username = getIntent().getStringExtra("USERNAME");

        FragmentManager fragmentManager = getSupportFragmentManager();
        HomeFragment homeFragment = new HomeFragment();

        // Mengirim username ke HomeFragment
        Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        homeFragment.setArguments(bundle);

        Fragment fragment = fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName());
        if (!(fragment instanceof HomeFragment)){
            fragmentManager
                    .beginTransaction()
                    .add(R.id.frame_container, homeFragment, HomeFragment.class.getSimpleName())
                    .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.navmenu);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.homebtn) {
                selectedFragment = new HomeFragment();
                selectedFragment.setArguments(bundle);
            } else if (item.getItemId() == R.id.favhbtn) {
                selectedFragment = new FavoriteFragment();
            } else if (item.getItemId() == R.id.profilebtn) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, selectedFragment)
                        .commit();

                return true;
            } else {
                return false;
            }
        });
    }
}
