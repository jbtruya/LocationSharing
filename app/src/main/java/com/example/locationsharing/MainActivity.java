package com.example.locationsharing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import Fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private boolean isPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        loadLoginFragment();
    }
    private void loadLoginFragment(){
        Fragment mFragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.slide_out,
                        R.anim.slide_in,
                        R.anim.slide_out
                )
                .replace(R.id.frame_login, mFragment)
                .commit();
    }
    private void loadLocationSharingActivity(){
        Intent mIntent = new Intent(MainActivity.this, LocationSharingActivity.class);
        finishAffinity();
        startActivity(mIntent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        if(isPermissionGranted){
            if(checkGooglePlayServices()){
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null) {
                    loadLocationSharingActivity();
                    Toast.makeText(MainActivity.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                }
            }else{

            }

        }

    }
    private void checkPermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent mIntent = new Intent();
                mIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri mUri = Uri.fromParts("package",getPackageName(),"");
                mIntent.setData(mUri);
                startActivity(mIntent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    private boolean checkGooglePlayServices(){
        GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        int result = mGoogleApiAvailability.isGooglePlayServicesAvailable(MainActivity.this);
        if(result == ConnectionResult.SUCCESS){
            return true;
        }else if(mGoogleApiAvailability.isUserResolvableError(result)){
            Dialog dialog = mGoogleApiAvailability.getErrorDialog(MainActivity.this, result, 420, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(MainActivity.this, "Action Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
        return false;
    }
}