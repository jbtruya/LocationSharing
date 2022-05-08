package Adapters;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Models.Comment;
import Models.Notification;
import Models.User;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.MyViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;
    private ArrayList<Notification> arrListNotification;

    public NotificationRecyclerViewAdapter(Context mContext, ArrayList<Notification> arrListNotification) {
        this.mContext = mContext;
        this.arrListNotification = arrListNotification;
    }

    @NonNull
    @Override
    public NotificationRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_layout_notifications, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRecyclerViewAdapter.MyViewHolder holder, int position) {
        Notification notification = arrListNotification.get(position);

        holder.text_title.setText(notification.getNotificationTitle());
        holder.text_body.setText(notification.getNotificationBody());


        db.collection("User Information")
                .document(notification.getUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isComplete()){
                            User user = task.getResult().toObject(User.class);
                            if(user.getAvatar() != null){
                                Glide.with(mContext).load(user.getAvatar()).into(holder.image);
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return arrListNotification.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text_title, text_body;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            text_title = itemView.findViewById(R.id.notifLayout_text_title);
            text_body = itemView.findViewById(R.id.notifLayout_text_body);

            image = itemView.findViewById(R.id.notifLayout_imageView);
        }
    }
}
