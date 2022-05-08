package Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.locationsharing.R;
import com.example.locationsharing.SignUpActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;

import Models.Sharedlocation;
import Models.User;

public class ShareFragment extends Fragment {

    private View mView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private Dialog mDialog;

    private User mUser;
    private Sharedlocation mSharedLocation;
    private ActivityResultLauncher<String> mActivityResultLauncher;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private EditText edt_title;
    private EditText edt_description;
    private TextView text_tap;
    private ImageView image_shared_photo;
    private Button btn_share_location;

    private String TAG = "SHARE_FRAGMENT_DEBUG_TAG";
    public ShareFragment() {
    }

    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       mView = inflater.inflate(R.layout.fragment_share, container, false);

        // Initialize Variables
        initializeVariables();
        // initialize Views
        initializeViews();
        // Initialize Custom Loading Dialog
        initializeCustomDialog();
        // Get User Information
        getUserInformation();
        // Add Photo Function
        addPhotoFunction();
        // Share Location Function
        btnShareLocationFunction();
        return mView;
    }
    private void initializeVariables(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        mSharedLocation = new Sharedlocation();
        mDialog = new Dialog(getContext());

    }
    private void initializeViews(){
        edt_title = mView.findViewById(R.id.edt_title);
        edt_description = mView.findViewById(R.id.edt_description);
        text_tap = mView.findViewById(R.id.share_text_tap);
        image_shared_photo = mView.findViewById(R.id.image_share);
        btn_share_location = mView.findViewById(R.id.btn_share_loaction);
    }
    private void initializeCustomDialog(){
        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_loading);
        mDialog.setContentView(R.layout.dialog_loading);
        mDialog.setCancelable(false);
    }
    private void getUserInformation(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        DocumentReference docRef = db.collection("User Information").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        mUser = document.toObject(User.class);
                    }
                    else{
                        Log.d(TAG, "DocumentSnapshot Failed: Document Does Not Exists");
                    }
                }else{
                    Log.d(TAG, "DocumentSnapshot Failed: " + task.getException());
                }
            }
        });
    }
    private void btnShareLocationFunction(){
        btn_share_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkForEmptyEditText()){
                    mDialog.show();
                    createSharedLocationRecord(edt_title.getText().toString(),edt_description.getText().toString());
                }else{
                    Toast.makeText(getContext(), "Please Don't leave anything blank", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void addPhotoFunction(){
        image_shared_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityResultLauncher.launch("image/*");
            }
        });

        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result != null){
                    mSharedLocation.setLocationPhoto(result.toString());
                    Glide.with(getContext()).load(result).into(image_shared_photo);
                    text_tap.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private boolean checkForEmptyEditText(){
        if(!TextUtils.isEmpty(edt_title.getText()) && !TextUtils.isEmpty(edt_description.getText())){
            return true;
        }else{
            return false;
        }
    }
    @SuppressLint("MissingPermission")
    private void createSharedLocationRecord(String title, String description){
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(mSharedLocation.getLocationPhoto() != null){
                    uploadPhotoToFireStorage(location, title, description);
                }else{
                    DocumentReference documentReference = db.collection("Shared Location").document();
                    Date currentTime = Calendar.getInstance().getTime();

                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mSharedLocation.setGeoPoint(geoPoint);
                    mSharedLocation.setUser(mUser);
                    mSharedLocation.setLocationTitle(title);
                    mSharedLocation.setLocationDescription(description);
                    mSharedLocation.setDate(currentTime);
                    mSharedLocation.setDocumentId(documentReference.getId());

                    documentReference.set(mSharedLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Location Shared!", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error Location Not Shared!", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void uploadPhotoToFireStorage(Location location, String title, String description){
        Date currentTime = Calendar.getInstance().getTime();

        StorageReference mFileRef = mStorageReference.child("Users/"+mUser.getUid()+"/Shared Location Photos/"+mUser.getUid()+currentTime+".jpg");
        mFileRef.putFile(Uri.parse(mSharedLocation.getLocationPhoto()))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DocumentReference documentReference = db.collection("Shared Location").document();
                                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                Date currentTime = Calendar.getInstance().getTime();

                                mSharedLocation.setGeoPoint(geoPoint);
                                mSharedLocation.setLocationPhoto(uri.toString());
                                mSharedLocation.setUser(mUser);
                                mSharedLocation.setDate(currentTime);
                                mSharedLocation.setLocationTitle(title);
                                mSharedLocation.setLocationDescription(description);
                                mSharedLocation.setDocumentId(documentReference.getId());

                                documentReference.set(mSharedLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Location Shared!", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error Location Not Shared!", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
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
}