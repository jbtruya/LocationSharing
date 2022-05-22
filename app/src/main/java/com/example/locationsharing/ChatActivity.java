package com.example.locationsharing;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Adapters.ChatRecyclerAdapter;
import Helpers.FcmSendMessageNotification;
import Models.Conversation;
import Models.Message;
import Models.Sharedlocation;
import Models.User;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private ListenerRegistration registration;

    private String receivedId;

    private ImageView imageView_avatar;
    private TextView text_userName;
    private TextInputLayout input_message;
    private RecyclerView recyclerView;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private ArrayList<Message> arrListMessage;

    private BottomSheetDialog mBottomSheetDialog;
    private ImageView image_attachPhoto;
    private ImageView image_photoAdd;
    private ProgressBar progressBar_uploading_image;

    private ActivityResultLauncher<String> mActivityResultLauncherPhoto;
    private Uri mPhotoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initVariables();
        initializeViews();
        getDataFromBundle();

        initSendMessageFunction();
    }
    private void initVariables(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        arrListMessage = new ArrayList<>();
    }
    private void initializeViews(){
        imageView_avatar = findViewById(R.id.chatAct_image_avatar);
        text_userName = findViewById(R.id.chatAct_text_name);
        input_message = findViewById(R.id.chatAct_input_message);


        recyclerView = findViewById(R.id.chatAct_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatRecyclerAdapter = new ChatRecyclerAdapter(ChatActivity.this, ChatActivity.this, arrListMessage);
        recyclerView.setAdapter(chatRecyclerAdapter);
        recyclerView.scrollToPosition(arrListMessage.size() - 1);
    }
    private void getDataFromBundle(){
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        String dataFrom = bundle.getString("dataFrom");
        if(dataFrom == null){
            return;
        }

        switch (dataFrom){
            case "ViewSharedLocation":
                receivedId = bundle.getString("userReceiverId");
                displayReceiverInfo();
                getConversation();
                break;
            case "ConversationFragment":
                receivedId = bundle.getString("userReceiverId");
                displayReceiverInfo();
                getMessages(bundle.getParcelable("conversation"));
                break;
        }
    }
    private void initSendMessageFunction(){
        input_message.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConversationDocument();
            }
        });

        input_message.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivityResultLauncherPhoto.launch("image/*");
            }
        });

        mActivityResultLauncherPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    showSendImageDialog();
                    mPhotoUri = result;
                    Glide.with(ChatActivity.this).load(result).into(image_attachPhoto);
                    image_photoAdd.setVisibility(View.GONE);
                }
            }
        });
    }
    private void showSendImageDialog() {
        mBottomSheetDialog = new BottomSheetDialog(ChatActivity.this);
        mBottomSheetDialog.setContentView(R.layout.bottomsheet_layout_chat_sendimage);
        mBottomSheetDialog.show();

        image_attachPhoto = mBottomSheetDialog.findViewById(R.id.chatFrag_btmsheet_image);
        image_photoAdd = mBottomSheetDialog.findViewById(R.id.chatFrag_btmsheet_imageAdd);
        progressBar_uploading_image = mBottomSheetDialog.findViewById(R.id.chatFrag_btmSheet_progressBar);
        Button btn_sendImage = mBottomSheetDialog.findViewById(R.id.chatFrag_btmsheet_btn);

        btn_sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhotoUri != null) {
                    checkConversationDocument();
                }
            }
        });
    }
    private void displayReceiverInfo(){
        db.collection("User Information").document(receivedId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            if(user.getAvatar() != null){
                                Glide.with(ChatActivity.this).load(user.getAvatar()).into(imageView_avatar);
                            }
                            text_userName.setText(user.getFirstname()+" "+user.getLastname());
                        }
                    }
                });
    }
    private void getConversation(){
        CollectionReference collectionReference = db.collection("Conversation");

        collectionReference.whereArrayContains("userId", receivedId);
        collectionReference.whereArrayContains("userId", mAuth.getCurrentUser().getUid());

        registration = collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                if(arrListMessage.isEmpty()){
                                    getMessages(conversation);
                                }
                                registration.remove();
                                break;
                        }
                    }
                }
            }
        });
    }
    private void getMessages(Conversation conversation){
        arrListMessage.clear();
        db.collection("Conversation").document(conversation.getConversationDocId())
                .collection("Messages").whereEqualTo("conversationDocId", conversation.getConversationDocId())
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            return;
                        }
                        for(DocumentChange doc: value.getDocumentChanges()){
                            switch (doc.getType()){
                                case ADDED:
                                    Log.d("CHAT_TEST", ""+doc.getDocument().toObject(Message.class).getSenderId());
                                    arrListMessage.add(doc.getDocument().toObject(Message.class));
                                    break;
                            }
                        }
                        chatRecyclerAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(arrListMessage.size() - 1);
                    }
                });
    }
    private void checkConversationDocument(){
        CollectionReference collectionReference = db.collection("Conversation");

        collectionReference.whereArrayContains("userId", receivedId);
        collectionReference.whereArrayContains("userId", mAuth.getCurrentUser().getUid());

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){
                        updateLatestMessage(task.getResult().getDocuments().get(0).toObject(Conversation.class));
                    }else{
                        createNewConversationDocument();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void createNewConversationDocument(){
        Conversation conversation = new Conversation();
        DocumentReference documentReference;
        documentReference = db.collection("Conversation").document();

        ArrayList<String> participants = new ArrayList<>();
        participants.add(mAuth.getCurrentUser().getUid());
        participants.add(receivedId);

        conversation.setConversationDocId(documentReference.getId());
        conversation.setConversationStatus("Open");

        conversation.setUserId(participants);

        if(mPhotoUri != null){
            conversation.setLatestMessage("Sent a photo.");
        }else{
            conversation.setLatestMessage(input_message.getEditText().getText().toString());
        }

        documentReference.set(conversation)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(mPhotoUri != null){
                            uploadResQPhoto(mPhotoUri, conversation);
                        }else{
                            sendMessage(conversation);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void updateLatestMessage(Conversation conversation){
        String message;
        if(mPhotoUri != null){
            message = "Sent a photo";
        }else{
            message = input_message.getEditText().getText().toString();
        }
        db.collection("Conversation")
                .document(conversation.getConversationDocId())
                .update("latestMessage", message)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(mPhotoUri != null){
                            uploadResQPhoto(mPhotoUri, conversation);
                        }else{
                            sendMessage(conversation);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void sendMessage(Conversation conversation){
        Date currentTime = Calendar.getInstance().getTime();
        DocumentReference documentReference;
        documentReference = db.collection("Conversation").document(conversation.getConversationDocId())
                .collection("Messages").document();
        Message message = new Message();

        message.setConversationDocId(conversation.getConversationDocId());
        message.setSenderId(mAuth.getCurrentUser().getUid());
        message.setDate(currentTime);
        message.setMessage(input_message.getEditText().getText().toString());
        message.setPhotoUri(null);


        documentReference.set(message)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        input_message.getEditText().getText().clear();
                        sendFcmNotification(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        input_message.getEditText().getText().clear();
                    }
                });
    }
    private void uploadResQPhoto(Uri uri, Conversation conversation){
        progressBar_uploading_image.setVisibility(View.VISIBLE);
        Date currentTime = Calendar.getInstance().getTime();
        String formattedCurrentTime = currentTime.toString().replace(" ","");

        StorageReference mFileRef = mStorageReference.child("Users/"+mAuth.getCurrentUser().getUid()+"/Message Photos/"+mAuth.getCurrentUser().getUid()+formattedCurrentTime+".jpg");
        mFileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Toast.makeText(getContext(), "gina upload ang image", Toast.LENGTH_SHORT).show();
                        mPhotoUri = null;
                        sendPhoto(uri.toString(), conversation);
                        progressBar_uploading_image.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    private void sendPhoto(String uri, Conversation conversation){
        Date currentTime = Calendar.getInstance().getTime();
        DocumentReference documentReference;
        documentReference = db.collection("Conversation").document(conversation.getConversationDocId())
                .collection("Messages").document();
        Message message = new Message();

        message.setConversationDocId(conversation.getConversationDocId());
        message.setSenderId(mAuth.getCurrentUser().getUid());
        message.setDate(currentTime);
        message.setMessage(null);
        message.setPhotoUri(uri);


        documentReference.set(message)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mBottomSheetDialog.dismiss();
                        input_message.getEditText().getText().clear();
                        sendFcmNotification(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mBottomSheetDialog.dismiss();
                        input_message.getEditText().getText().clear();
                    }
                });
    }
    private void sendFcmNotification(Message message){
        String receiverToken = "/topics/"+receivedId;

        db.collection("User Information").document(receivedId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User receiver = task.getResult().toObject(User.class);
                            int notificationId = (int) System.currentTimeMillis();
                            String body;
                            if(message.getPhotoUri() != null){
                                body = "Sent a photo.";
                            }else{
                                body = message.getMessage();
                            }

                            FcmSendMessageNotification sendFcm
                                    = new FcmSendMessageNotification(receiverToken,
                                    receiver.getFirstname()+" "+receiver.getLastname(),
                                    body,
                                    notificationId,
                                    message.getConversationDocId(),
                                    ChatActivity.this,
                                    ChatActivity.this);

                            sendFcm.sendNotification();
                        }
                    }
                });
    }
}