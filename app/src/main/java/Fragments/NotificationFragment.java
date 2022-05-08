package Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.locationsharing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import Adapters.CommentsRecyclerAdapter;
import Adapters.NotificationRecyclerViewAdapter;
import Models.Notification;


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