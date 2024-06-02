package com.example.infomovie;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.infomovie.fragment.MovieFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Pastikan untuk mengatur layout activity_main

        FragmentManager fragmentManager = getSupportFragmentManager();
        MovieFragment movieFragment = new MovieFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(MovieFragment.class.getSimpleName());
        if (!(fragment instanceof MovieFragment)){
            fragmentManager
                    .beginTransaction()
                    .add(R.id.frame_container, movieFragment, MovieFragment.class.getSimpleName())
                    .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.navmenu);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.homebtn) {
                selectedFragment = new MovieFragment();
            } else if (item.getItemId() == R.id.searchbtn) {
                // Tambahkan fragment untuk Search
                // selectedFragment = new SearchFragment();
            } else if (item.getItemId() == R.id.profilebtn) {
                // Tambahkan fragment untuk Profile
                // selectedFragment = new ProfileFragment();
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
