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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Models.Sharedlocation;

public class RecentSharedLocationRecyclerAdapter extends RecyclerView.Adapter<RecentSharedLocationRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Sharedlocation> mSharedLocation;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public RecentSharedLocationRecyclerAdapter(Context mContext, ArrayList<Sharedlocation> mSharedLocation) {
        this.mContext = mContext;
        this.mSharedLocation = mSharedLocation;
    }


    @NonNull
    @Override
    public RecentSharedLocationRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_layout_recent_shared, parent, false);
        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSharedLocationRecyclerAdapter.MyViewHolder holder, int position) {
        Sharedlocation sharedlocation =mSharedLocation.get(position);

        Long timeStamp = sharedlocation.getTimeStamp().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String convertedDate = simpleDateFormat.format(new Date(timeStamp));

        holder.text_title.setText(sharedlocation.getLocationTitle());
        holder.text_description.setText(sharedlocation.getLocationDescription());
        holder.text_date.setText(convertedDate);

        if(sharedlocation.getLocationPhoto() != null){
            Glide.with(mContext).load(sharedlocation.getLocationPhoto()).into(holder.image_shared);
        }


    }

    @Override
    public int getItemCount() {
        return mSharedLocation.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView text_title;
        private TextView text_description;
        private TextView text_date;

        private ImageView image_shared;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            text_title = itemView.findViewById(R.id.recycler_recent_title);
            text_description = itemView.findViewById(R.id.recycler_recent_description);
            text_date = itemView.findViewById(R.id.recycler_recent_date);

            image_shared = itemView.findViewById(R.id.recycler_recent_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
