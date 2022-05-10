package Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.locationsharing.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import Adapters.CommentsRecyclerAdapter;
import Adapters.NotificationRecyclerViewAdapter;
import Models.Notification;
import Models.Sharedlocation;
import Models.User;


public class NotificationFragment extends Fragment {

    private View mView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<Notification> arrListNotification;

    private RecyclerView recyclerView;
    private NotificationRecyclerViewAdapter recyclerAdapter;

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notification, container, false);

        initializeVariables();
        initializeViews();

        getNotifications();
        return  mView;
    }
    private void initializeVariables(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        arrListNotification = new ArrayList<>();
    }
    private void initializeViews(){
        recyclerView = mView.findViewById(R.id.notifFrag_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerAdapter = new NotificationRecyclerViewAdapter(getContext(),arrListNotification);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerAdapter.setOnItemClickListener(new NotificationRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                loadViewSharedLocation(arrListNotification.get(position));
            }
        });

       new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
           @Override
           public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
               return false;
           }

           @Override
           public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                recyclerAdapter.deleteItem(viewHolder.getAdapterPosition());
               getNotifications();
           }
       }).attachToRecyclerView(recyclerView);
    }
    private void loadViewSharedLocation(Notification notification){
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        progressDialog.setTitle("Loading Please Wait.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("Shared Location")
                .document(notification.getDocumentId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Sharedlocation sharedlocation = task.getResult().toObject(Sharedlocation.class);
                            db.collection("User Information")
                                    .document(mAuth.getCurrentUser().getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                User user = task.getResult().toObject(User.class);

                                                Bundle mBundle = new Bundle();
                                                mBundle.putParcelable("Shared Location", sharedlocation);
                                                mBundle.putString("dataFrom", "NewComment");
                                                mBundle.putParcelable("mUser", user);
                                                Fragment mFragment = new ViewSharedLocationFragment();
                                                mFragment.setArguments(mBundle);

                                                progressDialog.dismiss();
                                                getActivity().getSupportFragmentManager()
                                                        .beginTransaction()
                                                        .setCustomAnimations(
                                                                R.anim.slide_in,
                                                                R.anim.slide_out,
                                                                R.anim.slide_in,
                                                                R.anim.slide_out
                                                        )
                                                        .replace(R.id.notiFrag_frameLayout, mFragment)
                                                        .commit();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void getNotifications(){
        db.collection("Notifications")
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            return;
                        }
                        for(DocumentChange dc : value.getDocumentChanges()){
                            switch (dc.getType()){
                                case ADDED:
                                case MODIFIED:
                                    arrListNotification.add(dc.getDocument().toObject(Notification.class));
                                    break;
                                case REMOVED:
                                    break;
                            }
                            recyclerAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}