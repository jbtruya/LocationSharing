package com.example.locationsharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import Fragments.MapFragment;
import Fragments.ShareFragment;
import Fragments.UserFragment;

public class LocationSharingActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_sharing);
        // Bind Views;
        initializeBindViews();
        // Bottom Navigation Bar Functions
        bottomNavFunctions();
    }
    private void initializeBindViews(){
        mBottomNav = findViewById(R.id.bottomNavView);
    }
    private void loadInitialFragment(){
        Fragment mFragment = null;
        mFragment = new MapFragment();
        loadFragment(mFragment);
    }
    public void clearBackstack() {
        getSupportFragmentManager().popBackStack(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }
    private void bottomNavFunctions(){
        loadInitialFragment();
        clearBackstack();
        mBottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment mFragment = null;
                switch (item.getItemId()){
                    case R.id.nav_map:
                        clearBackstack();
                        mFragment = new MapFragment();
                        break;
                    case R.id.nav_share:
                        clearBackstack();
                        mFragment = new ShareFragment();
                        break;
                    case R.id.nav_user:
                        clearBackstack();
                        mFragment = new UserFragment();
                        break;
                }
                loadFragment(mFragment);
                return true;
            }
        });
    }
    private void loadFragment(Fragment mFragment){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.slide_out,
                        R.anim.slide_in,
                        R.anim.slide_out
                )
                .replace(R.id.frame_main, mFragment)
                .commit();
    }
}