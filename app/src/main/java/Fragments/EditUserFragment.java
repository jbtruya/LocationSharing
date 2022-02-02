package Fragments;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.locationsharing.R;
import com.example.locationsharing.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import Models.User;

public class EditUserFragment extends Fragment {

    private View mView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private Dialog mDialog;
    private Dialog mPasswordDialog;

    private User mUser;
    private ActivityResultLauncher<String> mActivityResultLauncher;

    private EditText edt_firstname;
    private EditText edt_lastname;
    private EditText edt_email;

    private TextView text_name;
    private TextView text_email;

    private ImageView image_avatar;

    private Button btn_update;
    private Button btn_change_password;

    private Uri uriAvatar;

    public EditUserFragment() {

    }
    public static EditUserFragment newInstance(String param1, String param2) {
        EditUserFragment fragment = new EditUserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_edit_user, container, false);
        // Initialize Variables
        initializeVariables();
        // Initialize Views
        initializeBindViews();
        //Initialize Dialog
        initializeCustomDialog();
        // Populate Views with data
        populateViewsWithData();
        // Tap Avatar Function
        imageAvatarFunction();
        // Button Update Function
        btnUpdateFunction();
        // Button Update Password Function
        btnUpdatePasswordFunction();
        return mView;
    }
    private void initializeVariables(){
        Bundle mBundle = this.getArguments();
        mUser = mBundle.getParcelable("User");
        mDialog = new Dialog(getContext());
        mPasswordDialog = new Dialog(getContext());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }
    private void initializeBindViews(){
        edt_firstname = mView.findViewById(R.id.edt_edit_firstname);
        edt_lastname = mView.findViewById(R.id.edt_edit_lastname);
        edt_email = mView.findViewById(R.id.edt_edit_email);

        text_name = mView.findViewById(R.id.user_edit_text_name);
        text_email = mView.findViewById(R.id.user_edit_text_email);

        image_avatar = mView.findViewById(R.id.image_edit_user_avatar);

        btn_update = mView.findViewById(R.id.btn_update);
        btn_change_password = mView.findViewById(R.id.btn_update_password);
    }
    private void initializeCustomDialog(){
        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_loading);
        mDialog.setContentView(R.layout.dialog_loading);
        mDialog.setCancelable(false);

        mPasswordDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_loading);
        mPasswordDialog.setContentView(R.layout.dialog_change_password);
        mPasswordDialog.setCancelable(false);
        changePasswordDialogFunctions();

    }
    private void changePasswordDialogFunctions(){
        Button btn_confirm, btn_cancel;
        EditText edt_password, edt_confirm_password;

        btn_confirm = mPasswordDialog.findViewById(R.id.btn_dialog_confirm);
        btn_cancel = mPasswordDialog.findViewById(R.id.btn_dialog_cancel);

        edt_password = mPasswordDialog.findViewById(R.id.edt_dialog_password);
        edt_confirm_password = mPasswordDialog.findViewById(R.id.edt_dialog_confirm_password);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(edt_password.getText().toString()) && !TextUtils.isEmpty(edt_confirm_password.getText().toString())){
                    if(edt_password.getText().toString().length() >= 6){
                        if(edt_password.getText().toString().equals(edt_confirm_password.getText().toString())){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(edt_password.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mPasswordDialog.dismiss();
                                                Toast.makeText(getContext(), "Password Changed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(getContext(), "Password Does not match", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "Password too short", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Please don't leave anything blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordDialog.dismiss();
            }
        });
    }
    private void populateViewsWithData(){
        text_name.setText(mUser.getFirstname()+" "+mUser.getLastname());
        text_email.setText(mUser.getEmail());

        edt_firstname.setText(mUser.getFirstname());
        edt_lastname.setText(mUser.getLastname());
        edt_email.setText(mUser.getEmail());

        if(getActivity() != null){
            if(mUser.getAvatar() != null){
                Glide.with(getContext()).load(mUser.getAvatar()).into(image_avatar);
            }
        }
    }
    private void populateViewsWithUpdatedData(){
        text_name.setText(edt_firstname.getText().toString()+" "+edt_lastname.getText().toString());
        text_email.setText(edt_email.getText().toString());
    }
    private void imageAvatarFunction(){
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
                    uriAvatar = result;
                    if(getActivity() != null){
                        Glide.with(getContext()).load(result).into(image_avatar);
                    }

                }
            }
        });
    }
    private void btnUpdateFunction(){
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkForEmptyEditText()){
                    mDialog.show();
                    updateUserRecord();
                }else{
                    Toast.makeText(getContext(), "Please don't leave anything blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void btnUpdatePasswordFunction(){
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordDialog.show();
            }
        });
    }
    private boolean checkForEmptyEditText(){
        if(!TextUtils.isEmpty(edt_firstname.getText().toString())
                && !TextUtils.isEmpty(edt_lastname.getText().toString())
                 && !TextUtils.isEmpty(edt_email.getText().toString())){
            return true;
        }else{
            return false;
        }
    }
    private boolean checkUserAvatar(){
        if(uriAvatar != null){
            return true;
        }else{
            return false;
        }
    }
    private boolean checkUserEmail(){
        if(!edt_email.getText().toString().equals(mUser.getEmail())){
            return true;
        }else{
            return false;
        }
    }
    private void updateUserRecord(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("User Information").document(firebaseUser.getUid());

        if(checkUserEmail()){
            docRef.update("firstname", edt_firstname.getText().toString(),
                    "lastname", edt_lastname.getText().toString(),
                                        "email", edt_email.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateUserEmail();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "User info update failed", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            docRef.update("firstname", edt_firstname.getText().toString(),
                    "lastname", edt_lastname.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if(checkUserAvatar()){
                                uploadNewUserAvatar();
                            }else{
                                populateViewsWithUpdatedData();
                                mDialog.dismiss();
                                Toast.makeText(getContext(), "User info updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "User info update failed", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }
    private void updateUserEmail(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.updateEmail(edt_email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(checkUserAvatar()){
                                uploadNewUserAvatar();
                            }else{
                                populateViewsWithUpdatedData();
                                mDialog.dismiss();
                                Toast.makeText(getContext(), "User info updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    private void uploadNewUserAvatar(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("User Information").document(firebaseUser.getUid());

        StorageReference mFileRef = mStorageReference.child("Users/"+firebaseUser.getUid()+"/Avatar"+".jpg");
        mFileRef.putFile(uriAvatar)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                docRef.update("avatar", uri.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                populateViewsWithUpdatedData();
                                                mDialog.dismiss();
                                                Toast.makeText(getContext(), "User info updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "User info update failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void updateUserPassword(String password){

    }
}