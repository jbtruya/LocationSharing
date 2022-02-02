package com.example.locationsharing;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import Models.User;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private Dialog mDialog;

    private ActivityResultLauncher<String> mActivityResultLauncher;

    private EditText edt_firstName;
    private EditText edt_lastName;
    private EditText edt_email;
    private EditText edt_password;
    private EditText edt_confirm_password;

    private TextView text_tap;

    private ImageView image_avatar;

    private User mUser;

    private String TAG = "SIGNUP_ACTIVITY_DEBUG_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialize Variables
        initializeVariables();
        // Initialize Dialog
        initializeCustomDialog();
        // Bind Views
        initializeBindViews();
        // Tap on Avatar Function
        setUserAvatarFunction();
    }
    private void initializeVariables(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mUser = new User();
        mDialog = new Dialog(SignUpActivity.this);
    }
    private void initializeBindViews(){
         edt_firstName = findViewById(R.id.edt_firstname);
         edt_lastName = findViewById(R.id.edt_lastname);
         edt_email = findViewById(R.id.edt_email);
         edt_password = findViewById(R.id.edt_password);
         edt_confirm_password = findViewById(R.id.edt_confirm_password);

         text_tap = findViewById(R.id.signup_text_tap);

         image_avatar = findViewById(R.id.image_avatar);
    }
    private void initializeCustomDialog(){
            mDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_loading);
            mDialog.setContentView(R.layout.dialog_loading);
            mDialog.setCancelable(false);
    }
    public void onClickBtnSignUpSubmit(View view){
            if(checkForEmptyEditText()){
                if(checkPasswordLength()){
                    if(checkPasswordMatch()){
                        mDialog.show();
                        checkEmail();
                    }else{
                        Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show();
                    }
            }else{
                Toast.makeText(this, "Please don't leave anything blank", Toast.LENGTH_SHORT).show();
            }
    }
    private void setUserAvatarFunction(){
            image_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivityResultLauncher.launch("image/*");
                }
            });

        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result != null){
                    mUser.setAvatar(result.toString());
                    Glide.with(SignUpActivity.this).load(result).into(image_avatar);
                    text_tap.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
    private boolean checkForEmptyEditText(){
        if(!TextUtils.isEmpty(edt_firstName.getText()) && !TextUtils.isEmpty(edt_lastName.getText()) &&
            !TextUtils.isEmpty(edt_email.getText()) && !TextUtils.isEmpty(edt_password.getText()) &&
                !TextUtils.isEmpty(edt_confirm_password.getText())){
            return true;
        }else{
            return false;
        }
    }
    private boolean checkPasswordLength(){
        if(edt_password.getText().toString().length() < 6){
            return false;
        }else{
            return true;
        }
    }
    private boolean checkPasswordMatch(){
        if(edt_password.getText().toString().equals(edt_confirm_password.getText().toString())){
            return true;
        }else{
            return false;
        }
    }
    private void checkEmail() {
        mAuth.fetchSignInMethodsForEmail(edt_email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean newEmail = task.getResult().getSignInMethods().isEmpty();
                        if (newEmail) {
                            createUserAuthRecord(edt_email.getText().toString(), edt_password.getText().toString());
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Email Already Used", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUserAuthRecord(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){
                          Log.d(TAG,"createUserWithEmail:success");
                          FirebaseUser user = mAuth.getCurrentUser();

                          mUser.setUid(user.getUid());
                          mUser.setFirstname(edt_firstName.getText().toString());
                          mUser.setLastname(edt_lastName.getText().toString());
                          mUser.setEmail(edt_email.getText().toString());

                          if(mUser.getAvatar() != null){
                              uploadAvatarToFireStorage(user);
                          }else{
                              insertUserInfo(user,mUser);
                          }

                      }else{

                      }
                    }
                });
    }
    private void insertUserInfo(FirebaseUser firebaseUser, User user){
        db.collection("User Information")
                .document(firebaseUser.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "User Created!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: User Information Document Created");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed Creating User Information Document");
                    }
                });

    }
    private void uploadAvatarToFireStorage(FirebaseUser user){
        StorageReference mFileRef = mStorageReference.child("Users/"+user.getUid()+"/Avatar"+".jpg");
        mFileRef.putFile(Uri.parse(mUser.getAvatar()))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mUser.setAvatar(uri.toString());
                                insertUserInfo(user,mUser);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}