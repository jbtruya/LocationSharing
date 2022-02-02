package com.example.locationsharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Dialog mDialog;

    private EditText edt_email;
    private EditText edt_password;

    private String TAG = "LOGIN_EMAIL_DEBUG_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        mAuth = FirebaseAuth.getInstance();

        //Initialize Variables
        initializeVariables();
        //Initialize Dialog
        initializeDialog();
        //Initialize Bind Views
        initializeBindViews();
    }
    private void initializeVariables(){
        mAuth = FirebaseAuth.getInstance();
        mDialog = new Dialog(LoginEmailActivity.this);
    }
    private void initializeBindViews(){
        edt_email = findViewById(R.id.edt_login_email);
        edt_password = findViewById(R.id.edt_login_password);
    }
    private void initializeDialog(){
        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_loading);
        mDialog.setContentView(R.layout.dialog_loading);
        mDialog.setCancelable(false);
    }
    public void onClickBtnLoginFunction(View view){
            if(checkForEmptyEditText()){
                mDialog.show();
                loginWithEmail(edt_email.getText().toString(), edt_password.getText().toString());
            }else{
                Toast.makeText(LoginEmailActivity.this, "Please don't leave anything blank", Toast.LENGTH_SHORT).show();
            }
    }
    private boolean checkForEmptyEditText(){
        if(!TextUtils.isEmpty(edt_email.getText().toString()) && !TextUtils.isEmpty(edt_password.getText().toString())){
            return true;
        }else{
            return false;
        }
    }
    private void loginWithEmail(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginEmailActivity.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            loadLocationSharingActivity();
                        }else{
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginEmailActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });
    }
    private void loadLocationSharingActivity(){
        Intent mIntent = new Intent(LoginEmailActivity.this, LocationSharingActivity.class);
        finishAffinity();
        startActivity(mIntent);
        finish();
    }
}