package Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.locationsharing.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Models.Conversation;
import Models.Message;
import Models.User;

public class ConversationRecyclerAdapter extends RecyclerView.Adapter<ConversationRecyclerAdapter.MyViewHolder> {

    private Context context;
    private FirebaseFirestore db;
    private Activity activity;
    private ArrayList<Conversation> arrListConversation;
    private FirebaseAuth mAuth;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public ConversationRecyclerAdapter(Context context, Activity activity, ArrayList<Conversation> arrListConversation) {
        this.context = context;
        this.activity = activity;
        this.arrListConversation = arrListConversation;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ConversationRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_layout_conversation, parent, false);
        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationRecyclerAdapter.MyViewHolder holder, int position) {
        Conversation conversation = arrListConversation.get(position);
        conversation.getUserId().remove(mAuth.getCurrentUser().getUid());
        String userId = conversation.getUserId().get(0);

        db.collection("User Information").document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            if(user.getAvatar() != null){
                                Glide.with(context).load(user.getAvatar()).into(holder.image_avatar);
                            }
                            holder.text_name.setText(user.getFirstname()+" "+user.getLastname());
                            holder.text_message.setText(conversation.getLatestMessage());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return arrListConversation.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image_avatar;
        TextView text_name, text_message;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            image_avatar = itemView.findViewById(R.id.recycler_layout_image_avatar);
            text_name = itemView.findViewById(R.id.recycler_layout_text_name);
            text_message = itemView.findViewById(R.id.recycler_layout_message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
