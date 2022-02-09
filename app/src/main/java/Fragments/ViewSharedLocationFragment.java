package Fragments;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.locationsharing.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Adapters.CommentsRecyclerAdapter;
import Adapters.RecentSharedLocationRecyclerAdapter;
import Models.Comment;
import Models.Sharedlocation;
import Models.User;


public class ViewSharedLocationFragment extends Fragment {

    private View mView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private Dialog mDialog;
    private Dialog mLoadingDialog;

    private CommentsRecyclerAdapter mCommentsRecyclerAdapter;
    private Sharedlocation mSharedLocation;
    private ArrayList<Comment> mCommentArrayList;
    private User mUser;
    private Comment mComment;

    private ActivityResultLauncher<String> mActivityResultLauncher;

    private TextView text_name;
    private TextView text_email;
    private TextView text_date;
    private TextView text_coordinates;
    private TextView text_title;
    private TextView text_description;

    private TextInputLayout input_comment;
    private RecyclerView mRecyclerView;

    private ImageView image_shared_photo;
    private ImageView image_avatar;
    private ImageView image_edit;
    private ImageView image_comment_avatar;

    private Uri dialogUri;
    private String dialogTitle;
    private String dialogDescription;

    public ViewSharedLocationFragment() {

    }


    public static ViewSharedLocationFragment newInstance(String param1, String param2) {
        ViewSharedLocationFragment fragment = new ViewSharedLocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_view_shared_location, container, false);
        // Initialize Variables
        initializeVariables();
        // Initialize Bind Views
        initializeBindViews();
        // Populate Views With Data

        Bundle bundle = getArguments();
        if(bundle != null){
            mSharedLocation = bundle.getParcelable("Shared Location");
            mUser = bundle.getParcelable("mUser");
            populateViewsWithData();
        }else{
            Toast.makeText(getContext(), "INA MO", Toast.LENGTH_SHORT).show();
        }
        // Get Comments
        getSharedLocationComments();

        // initialize Dialog
        initializeDialog();

        // image_edit function
        imageEditFunction();


        // Initialize Recycler
        initializeRecycler();
        checkForNewComments();
        return mView;
    }
    private void initializeVariables(){
        //Bundle mBundle = this.getArguments();

        //mSharedLocation = mBundle.getParcelable("Shared Location");
        mDialog = new Dialog(getContext());
        mLoadingDialog = new Dialog(getContext());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mCommentArrayList = new ArrayList<>();
    }
    private void initializeDialog(){
        mLoadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_loading);
        mLoadingDialog.setContentView(R.layout.dialog_loading);
        mLoadingDialog.setCancelable(false);



        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_loading);
        mDialog.setContentView(R.layout.dialog_edit_shared_location);
        mDialog.setCancelable(true);

        EditText dialog_title, dialog_description;
        ImageView dialog_photo, dialog_image_delete;
        Button btn_update;

        dialog_title = mDialog.findViewById(R.id.edt_shared_edit_dialog_title);
        dialog_description = mDialog.findViewById(R.id.edt_shared_edit_dialog_description);
        dialog_photo = mDialog.findViewById(R.id.image_dialog_shared_photo);
        dialog_image_delete = mDialog.findViewById(R.id.image_dialog_delete);
        btn_update = mDialog.findViewById(R.id.btn_dialog_edit_shared_update);

        dialog_title.setText(mSharedLocation.getLocationTitle());
        dialog_description.setText(mSharedLocation.getLocationDescription());

        if(getActivity() != null){
            Glide.with(getContext()).load(mSharedLocation.getLocationPhoto()).into(dialog_photo);
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if(!mSharedLocation.getUser().getUid().equals(user.getUid())){
           dialog_image_delete.setVisibility(View.INVISIBLE);
        }

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingDialog.show();
                dialogTitle = dialog_title.getText().toString();
                dialogDescription = dialog_description.getText().toString();

                if(dialogUri != null){
                    uploadUpdatedPhoto(dialogUri);
                }else{
                    updateSharedLocation(dialogTitle,dialogDescription);
                }
            }
        });
        dialog_image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSharedLocation();
            }
        });
        dialog_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityResultLauncher.launch("image/*");
            }
        });
        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result != null){
                    dialogUri = result;
                    if(getActivity() != null){
                        Glide.with(getContext()).load(result).into(dialog_photo);
                    }

                }
            }
        });

    }
    private void initializeBindViews(){
        text_name = mView.findViewById(R.id.view_shared_name);
        text_email = mView.findViewById(R.id.view_shared_email);
        text_date = mView.findViewById(R.id.view_shared_date);
        text_coordinates = mView.findViewById(R.id.view_shared_coordinates);
        text_title = mView.findViewById(R.id.view_shared_title);
        text_description = mView.findViewById(R.id.view_shared_description);

        image_shared_photo = mView.findViewById(R.id.view_shared_image);
        image_avatar = mView.findViewById(R.id.view_shared_avatar);
        image_edit = mView.findViewById(R.id.view_shared_edit);
        image_comment_avatar = mView.findViewById(R.id.view_shared_comment_avatar);

        mRecyclerView = mView.findViewById(R.id.view_shared_recyclerView);

        input_comment = mView.findViewById(R.id.view_shared_textField_comment);
        input_comment.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCommentDocument(input_comment.getEditText().getText().toString());
            }
        });
    }
    private void initializeRecycler(){
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCommentsRecyclerAdapter = new CommentsRecyclerAdapter(getContext(),mCommentArrayList);
        mRecyclerView.setAdapter(mCommentsRecyclerAdapter);
    }
    private void getSharedLocationComments(){
        db.collection("Comment Information")
                .whereEqualTo("sharedLocation.documentId", mSharedLocation.getDocumentId())
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            mCommentArrayList.clear();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                mCommentArrayList.add(documentSnapshot.toObject(Comment.class));
                            }
                            mCommentsRecyclerAdapter.notifyDataSetChanged();
                        }else{
                        }
                    }
                });
    }
    private void checkForNewComments(){
        db.collection("Comment Information")
                .whereEqualTo("sharedLocation.documentId", mSharedLocation.getDocumentId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentChange dc : value.getDocumentChanges()){
                            switch (dc.getType()){
                                case ADDED:
                                    getSharedLocationComments();
                                    break;
                                case REMOVED:
                                    break;
                                case MODIFIED:
                                    break;
                            }
                        }
                    }
                });
    }
    private void createCommentDocument(String comment){
        DocumentReference documentReference = db.collection("Comment Information").document();
        mComment = new Comment();
        mComment.setCommentDocId(documentReference.getId());
        mComment.setSharedLocation(mSharedLocation);
        mComment.setUser(mUser);
        mComment.setComment(comment);

        documentReference.set(mComment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Comment added!", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void populateViewsWithData(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(!mSharedLocation.getUser().getUid().equals(user.getUid())){
            image_edit.setVisibility(View.INVISIBLE);
        }

        if(mSharedLocation.getUser().getAvatar() != null) {
            if (getActivity() != null) {
                Glide.with(getContext()).load(mSharedLocation.getUser().getAvatar()).into(image_avatar);
            }
        }
        if(mUser.getAvatar() != null) {
            if (getActivity() != null) {
                Glide.with(getContext()).load(mUser.getAvatar()).into(image_comment_avatar);
            }
        }

        if(getActivity() != null){
            Glide.with(getContext()).load(mSharedLocation.getLocationPhoto()).into(image_shared_photo);
        }
            Long timeStamp = mSharedLocation.getTimeStamp().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String ConvertedDate = simpleDateFormat.format(new Date(timeStamp));

            text_name.setText(mSharedLocation.getUser().getFirstname()+" "+mSharedLocation.getUser().getLastname());
            text_email.setText(mSharedLocation.getUser().getEmail());
            text_date.setText(ConvertedDate);
            text_coordinates.setText(mSharedLocation.getGeoPoint().getLatitude()+" "+mSharedLocation.getGeoPoint().getLongitude());
            text_title.setText(mSharedLocation.getLocationTitle());
            text_description.setText(mSharedLocation.getLocationDescription());


    }
    private void populateViewsWithUpdatedData(){
        if (dialogUri != null) {
            if(getActivity() != null){
                Glide.with(getContext()).load(dialogUri).into(image_shared_photo);
            }
        }
        text_title.setText(dialogTitle);
        text_description.setText(dialogDescription);
    }
    private void imageEditFunction(){
        image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
            }
        });
    }
    private void deleteOldSharedPhoto(Uri uri){
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(mSharedLocation.getLocationPhoto());
        DocumentReference docRef = db.collection("Shared Location").document(mSharedLocation.getDocumentId());

        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                docRef.update("locationPhoto", uri.toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                updateSharedLocation(dialogTitle, dialogDescription);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Update Shared Location Failed", Toast.LENGTH_SHORT).show();
                        mLoadingDialog.dismiss();
                    }
                });
            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mLoadingDialog.dismiss();
                }
            });
    }
    private void uploadUpdatedPhoto(Uri uri){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Date currentTime = Calendar.getInstance().getTime();

        StorageReference mFileRef = mStorageReference.child("Users/"+mSharedLocation.getUser().getUid()+"/Shared Location Photos/"+mSharedLocation.getUser().getUid()+currentTime+".jpg");
        mFileRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                deleteOldSharedPhoto(uri);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoadingDialog.dismiss();
            }
        });
    }
    private void updateSharedLocation(String title, String description){
        DocumentReference docRef = db.collection("Shared Location").document(mSharedLocation.getDocumentId());
        docRef.update("locationTitle", title,
                "locationDescription",description)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Shared Location Updated!", Toast.LENGTH_SHORT).show();
                        populateViewsWithUpdatedData();
                        mLoadingDialog.dismiss();
                        mDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Update Shared Location Failed!", Toast.LENGTH_SHORT).show();
                mLoadingDialog.dismiss();
            }
        });
    }
    private void deleteSharedLocation(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(mSharedLocation.getLocationPhoto());
        DocumentReference docRef = db.collection("Shared Location").document(mSharedLocation.getDocumentId());

        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Shared Location Deleted", Toast.LENGTH_SHORT).show();
                                mLoadingDialog.dismiss();
                                mDialog.dismiss();
                                getActivity().onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoadingDialog.dismiss();
            }
        });
    }
}