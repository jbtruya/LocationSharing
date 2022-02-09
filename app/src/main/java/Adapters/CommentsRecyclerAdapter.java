package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.locationsharing.R;

import java.util.ArrayList;

import Models.Comment;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Comment> mComment;

    public CommentsRecyclerAdapter(Context mContext, ArrayList<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
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

        holder.edt_comment.setText(comment.getComment());
        holder.text_name.setText(comment.getUser().getFirstname()+" "+comment.getUser().getLastname());

        if(comment.getUser().getAvatar() != null){
            Glide.with(mContext).load(comment.getUser().getAvatar()).into(holder.image_avatar);
        }
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView text_name;
        private EditText edt_comment;
        private ImageView image_avatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.recycler_comments_name);
            edt_comment = itemView.findViewById(R.id.recycler_comments_edt_comment);
            image_avatar = itemView.findViewById(R.id.recycler_comments_image_avatar);
        }
    }
}
