package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.locationsharing.ChatActivity;
import com.example.locationsharing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Adapters.ConversationRecyclerAdapter;
import Models.Conversation;

public class ConversationFragment extends Fragment {

    private View mView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ConversationRecyclerAdapter conversationRecyclerAdapter;

    private ArrayList<Conversation> arrListConversation;
    public ConversationFragment() {
    }

    public static ConversationFragment newInstance(String param1, String param2) {
        ConversationFragment fragment = new ConversationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_conversation, container, false);

        initVariables();
        initViews();
        return mView;
    }
    private void initVariables(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        arrListConversation = new ArrayList<>();
    }
    private void initViews(){
        recyclerView = mView.findViewById(R.id.conversationFrag_recycler);

        conversationRecyclerAdapter = new ConversationRecyclerAdapter(getContext(), getActivity(),arrListConversation);
        recyclerView.setAdapter(conversationRecyclerAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        conversationRecyclerAdapter.setOnItemClickListener(new ConversationRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                arrListConversation.get(position).getUserId().remove(mAuth.getCurrentUser().getUid());
                String receiverId = arrListConversation.get(position).getUserId().get(0);

                intent.putExtra("userReceiverId", receiverId);
                intent.putExtra("dataFrom", "ViewSharedLocation");
                intent.putExtra("conversation", arrListConversation.get(position));

                startActivity(intent);
            }
        });

        getConversations();
    }
    private void getConversations(){
        CollectionReference collectionReference = db.collection("Conversation");

        collectionReference.whereArrayContains("userId", mAuth.getCurrentUser().getUid());

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                if(!value.isEmpty()){
                    for(DocumentChange doc: value.getDocumentChanges()){
                        switch (doc.getType()){
                            case ADDED:
                                Conversation conversation = doc.getDocument().toObject(Conversation.class);
                                if(conversation != null){
                                  arrListConversation.add(conversation);
                                }
                                break;
                        }
                    }
                    conversationRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}