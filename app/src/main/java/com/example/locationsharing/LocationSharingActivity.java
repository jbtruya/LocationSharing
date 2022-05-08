package com.example.locationsharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import Fragments.MapFragment;
import Fragments.NotificationFragment;
import Fragments.ShareFragment;
import Fragments.UserFragment;
import Fragments.ViewSharedLocationFragment;
import Models.Sharedlocation;
import Models.User;

public class LocationSharingActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_sharing);
        // Init Variables
        initializeVariables();
        getDataFromBundle();
        // Bind Views;
        initializeBindViews();
        // Bottom Navigation Bar Functions
        bottomNavFunctions();


    }
    private void initializeVariables(){
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getFcmToken();


    }
    private void getDataFromBundle(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String dataFrom = bundle.getString("dataFrom");
            if(dataFrom == null){
                loadInitialFragment();
                return;
            }
            switch (dataFrom){
                case "NewComment":
                    Sharedlocation sharedlocation = bundle.getParcelable("Shared Location");
                    User mUser = bundle.getParcelable("mUser");
                    loadViewSharedLocationFragment(sharedlocation,mUser);
                    break;
            }
        }else{
          loadInitialFragment();
        }
    }
    private void loadViewSharedLocationFragment(Sharedlocation sharedlocation, User mUser){
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("Shared Location", sharedlocation);
        mBundle.putString("dataFrom", "NewComment");
        mBundle.putParcelable("mUser", mUser);
        Fragment mFragment = new ViewSharedLocationFragment();
        mFragment.setArguments(mBundle);


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
    private void getFcmToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Fcm_Tag", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log
                        Log.d("Fcm_Tag", token);

                        db.collection("User Information")
                                .document(mAuth.getCurrentUser().getUid())
                                .update("fcmToken", token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                });
                    }
                });
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
                    case R.id.nav_notification:
                        clearBackstack();
                        mFragment = new NotificationFragment();
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