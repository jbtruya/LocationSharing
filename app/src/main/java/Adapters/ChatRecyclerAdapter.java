package Adapters;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.locationsharing.R;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Fragments.ViewImageFragment;
import Models.Message;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<Message> arrListMessage;
    private FirebaseAuth mAuth;

    public ChatRecyclerAdapter(Context context, Activity activity, ArrayList<Message> arrListMessage) {
        this.context = context;
        this.activity = activity;
        this.arrListMessage = arrListMessage;

        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ChatRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_layout_message, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerAdapter.MyViewHolder holder, int position) {
            Message message = arrListMessage.get(position);

            Long timeStamp = message.getDate().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a dd,MMM,''yy");
            String ConvertedDate = simpleDateFormat.format(new Date(timeStamp));

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            String time = timeFormat.format(new Date(timeStamp));

        if(mAuth.getCurrentUser().getUid().equals(message.getSenderId())){
            holder.linearLayout_sent.setVisibility(View.VISIBLE);
            holder.linearLayout_received.setVisibility(View.GONE);

            if(message.getPhotoUri() == null){
                holder.chip_sent.setVisibility(View.VISIBLE);
                holder.chip_sent.setText(message.getMessage());
                holder.image_sent.setVisibility(View.GONE);
                holder.text_time_sent.setVisibility(View.VISIBLE);
            }else{
                holder.image_sent.setVisibility(View.VISIBLE);
                holder.chip_sent.setVisibility(View.GONE);
                Glide.with(context).load(message.getPhotoUri()).into(holder.image_sent);
                holder.text_time_sent.setTranslationY(-2);
                holder.text_time_sent.setVisibility(View.VISIBLE);
            }


            if(DateUtils.isToday(timeStamp)){
                holder.text_time_sent.setText(time);

            }else{
                holder.text_time_sent.setText(ConvertedDate);
            }


        }else{
            holder.linearLayout_sent.setVisibility(View.GONE);
            holder.linearLayout_received.setVisibility(View.VISIBLE);

            if(message.getPhotoUri() == null){
                holder.chip_received.setVisibility(View.VISIBLE);
                holder.chip_received.setText(message.getMessage());
                holder.image_received.setVisibility(View.GONE);
                holder.text_time_received.setVisibility(View.VISIBLE);
            }else{
                holder.chip_received.setVisibility(View.GONE);
                Glide.with(context).load(message.getPhotoUri()).into(holder.image_received);
                holder.image_received.setVisibility(View.VISIBLE);
                holder.text_time_received.setTranslationY(-2);
                holder.text_time_received.setVisibility(View.VISIBLE);
            }

            if(DateUtils.isToday(timeStamp)){
                holder.text_time_received.setText(time);
            }else{
                holder.text_time_received.setText(ConvertedDate);
            }
        }

        holder.image_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ViewImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ImageUrl", message.getPhotoUri());
                fragment.setArguments(bundle);

                ((AppCompatActivity)activity).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,
                                R.anim.slide_out,
                                R.anim.slide_in,
                                R.anim.slide_out
                        )
                        .addToBackStack(null)
                        .replace(R.id.chatAct_frameLayout, fragment)
                        .commit();
            }
        });

        holder.image_received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ViewImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ImageUrl", message.getPhotoUri());
                fragment.setArguments(bundle);

                ((AppCompatActivity)activity).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,
                                R.anim.slide_out,
                                R.anim.slide_in,
                                R.anim.slide_out
                        )
                        .addToBackStack(null)
                        .replace(R.id.chatAct_frameLayout, fragment)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrListMessage.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private Chip chip_sent;
        private Chip chip_received;
        private TextView text_time_received;
        private TextView text_time_sent;
        private ImageView image_sent;
        private ImageView image_received;
        private ImageView btn_accept_received, btn_accept_sent;
        private TextView text_offer_received, text_amount_received;
        private TextView text_offer_sent, text_amount_sent;
        private CardView cardView_offer_received, cardView_offer_sent;
        private LinearLayout linearLayout_sent;
        private LinearLayout linearLayout_received;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chip_sent = itemView.findViewById(R.id.recyclerLayout_chat_message_chip_sent);
            chip_received = itemView.findViewById(R.id.recyclerLayout_chat_message_chip_received);
            text_time_received = itemView.findViewById(R.id.recyclerLayout_chat_message_date_received);
            text_time_sent = itemView.findViewById(R.id.recyclerLayout_chat_message_date_sent);
            image_sent = itemView.findViewById(R.id.recyclerLayout_chat_message_image_sent);
            image_received = itemView.findViewById(R.id.recyclerLayout_chat_message_image_received);
            linearLayout_sent = itemView.findViewById(R.id.recyclerLayout_chat_message_linearLayout_sent);
            linearLayout_received = itemView.findViewById(R.id.recyclerLayout_chat_message_linearLayout_received);
        }
    }
}
