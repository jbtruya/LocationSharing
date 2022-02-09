package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.locationsharing.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Models.Comment;
import Models.User;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.MyViewHolder> {

    private FirebaseFirestore db;
    private Context mContext;
    private User mUser;
    private ArrayList<Comment> mComment;

    public CommentsRecyclerAdapter(Context mContext, ArrayList<Comment> mComment, User mUser) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.mUser = mUser;

    }

    @NonNull
    @Override
    public CommentsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_layout_comments, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsRecyclerAdapter.MyViewHolder holder, int position) {
        Comment comment = mComment.get(position);

        holder.edt_comment.getEditText().setText(comment.getComment());
        holder.text_name.setText(comment.getUser().getFirstname()+" "+comment.getUser().getLastname());

        if(!mUser.getUid().equals(comment.getUser().getUid())){
            holder.edt_comment.setEndIconMode(TextInputLayout.END_ICON_NONE);
            holder.edt_comment.getEditText().setEnabled(false);
        }

        if(comment.getUser().getAvatar() != null){
            Glide.with(mContext).load(comment.getUser().getAvatar()).into(holder.image_avatar);
        }

        holder.edt_comment.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.edt_comment.getEditText().getText().toString().trim().equals(comment.getComment())){
                    updateUserComment(comment,holder.edt_comment.getEditText().getText().toString().trim());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView text_name;
        private TextInputLayout edt_comment;
        private ImageView image_avatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.recycler_comments_name);
            edt_comment = itemView.findViewById(R.id.recycler_comments_edt_comment);
            image_avatar = itemView.findViewById(R.id.recycler_comments_image_avatar);
        }
    }
    private void updateUserComment(Comment comment, String updatedComment){
        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Comment Information").document(comment.getCommentDocId());
        documentReference
                .update("comment", updatedComment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                    }
                });
    }
   public void deleteItem(int position){
       db = FirebaseFirestore.getInstance();

       db.collection("Comment Information")
               .document(mComment.get(position).getCommentDocId())
               .delete()
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
                   }
               });
   }
}
