package Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.locationsharing.LocationSharingActivity;
import com.example.locationsharing.LoginEmailActivity;
import com.example.locationsharing.MainActivity;
import com.example.locationsharing.R;
import com.example.locationsharing.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Adapters.RecentSharedLocationRecyclerAdapter;
import Models.Sharedlocation;
import Models.User;

public class UserFragment extends Fragment {

    private View mView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView text_name;
    private TextView text_email;
    private TextView text_no_recent_shared;

    private ImageView image_avatar;
    private ImageView image_btn_signout;
    private ImageView image_btn_edit;

    private RecyclerView mRecycler_recentShares;
    private RecentSharedLocationRecyclerAdapter mRecyclerRecentSharesAdapter;

    private ScrollView scrollView_User;

    private User mUser;

    private ArrayList<Sharedlocation> mSharedLocationArr;

    private String TAG = "USER_PROFILE_DEBUG_TAG";
    public UserFragment() {

    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         mView = inflater.inflate(R.layout.fragment_user, container, false);
         // Initialize Views
         initializeVariables();
         // Bind Views
         initializeBindViews();
         // Get User Information
         getUserInformation();
         // Get User Recent Shared Location
         getUserResQRequest();
         // Edit Button Function
         btnEditFunction();
         // Sign Out Button Function
         btnSignOutFunction();
        return mView;
    }
    private void initializeVariables(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = new User();

        mSharedLocationArr =  new ArrayList<>();
    }
    private void initializeBindViews(){
        text_name = mView.findViewById(R.id.user_text_name);
        text_email = mView.findViewById(R.id.user_text_email);
        text_no_recent_shared = mView.findViewById(R.id.user_text_3);

        image_avatar = mView.findViewById(R.id.image_user_avatar);
        image_btn_signout = mView.findViewById(R.id.image_btn_signout);
        image_btn_edit = mView.findViewById(R.id.image_btn_user_edit);

        scrollView_User = mView.findViewById(R.id.scrollView_User);

        mRecycler_recentShares = mView.findViewById(R.id.recycler_recentShares);
        initializeRecycler();
    }
    private void initializeRecycler(){
        mRecycler_recentShares.setHasFixedSize(true);
        mRecycler_recentShares.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerRecentSharesAdapter = new RecentSharedLocationRecyclerAdapter(getContext(),mSharedLocationArr);
        mRecycler_recentShares.setAdapter(mRecyclerRecentSharesAdapter);

        mRecycler_recentShares.addOnScrollListener(new RecyclerView.OnScrollListener() {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavView);
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0 && navBar.isShown()){
                    navBar.setVisibility(View.GONE);
                }else if(dy < 0){
                    navBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        mRecyclerRecentSharesAdapter.setOnItemClickListener(new RecentSharedLocationRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), mSharedLocationArr.get(position).getLocationTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getUserResQRequest(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        db.collection("Shared Location")
                .whereEqualTo("user.uid", firebaseUser.getUid())
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            mSharedLocationArr.clear();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                mSharedLocationArr.add(documentSnapshot.toObject(Sharedlocation.class));
                            }
                            mRecyclerRecentSharesAdapter.notifyDataSetChanged();
                            if(!mSharedLocationArr.isEmpty()){
                                text_no_recent_shared.setVisibility(View.INVISIBLE);
                            }
                        }else{
                            Log.d(TAG,"Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void getUserInformation(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DocumentReference mDocRef = db.collection("User Information").document(firebaseUser.getUid());
        mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        mUser = document.toObject(User.class);
                        setUserInformationToViews(mUser);
                    }else{
                        Log.d(TAG, "No such document");
                    }
                }else{
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private void setUserInformationToViews(User user){
        if(user.getAvatar() != null){
            text_name.setText(user.getFirstname()+" "+user.getLastname());
            text_email.setText(user.getEmail());
            if(getActivity() != null){
                Glide.with(getContext()).load(Uri.parse(user.getAvatar())).into(image_avatar);
            }
        }else{
            text_name.setText(user.getFirstname()+" "+user.getLastname());
            text_email.setText(user.getEmail());
        }
    }
    private void btnEditFunction(){
        image_btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mFragment = null;
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("User", mUser);
                mFragment = new EditUserFragment();
                mFragment.setArguments(mBundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,
                                R.anim.slide_out,
                                R.anim.slide_in,
                                R.anim.slide_out
                        )
                        //.replace(((ViewGroup)getView().getParent()).getId(), mFragment)
                        .replace(R.id.frame_main, mFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
    private void btnSignOutFunction(){
            image_btn_signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    loadMainActivity();
                }
            });
    }
    private void loadMainActivity(){
        Intent mIntent = new Intent(getContext(), MainActivity.class);
        startActivity(mIntent);
        getActivity().finish();
    }

}