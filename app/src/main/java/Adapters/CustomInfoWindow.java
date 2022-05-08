package Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.locationsharing.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;

import Models.Sharedlocation;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{
    private View mWindow;
    private Context mContext;

    private Sharedlocation mSharedLocation;

    private TextView text_title;
    private TextView text_description;
    private TextView text_name;
    private TextView text_date;
    private ImageView image_shared;

    public CustomInfoWindow(Context context){
        this.mContext = context;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.info_window_custom, null);
        mWindow.setClipToOutline(true);

        //Initialize Bind Views
        initializeBindViews();

        // Populate Views With Data
        populateViewsWithData(marker);
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
    private void initializeBindViews(){
        text_title = mWindow.findViewById(R.id.info_window_title);
        text_description = mWindow.findViewById(R.id.info_window_description);
        text_name = mWindow.findViewById(R.id.info_window_name);
        text_date = mWindow.findViewById(R.id.info_window_date);

        image_shared = mWindow.findViewById(R.id.info_window_image);
    }
    private void populateViewsWithData(Marker marker){
        Glide.with(mContext).load(mSharedLocation.getLocationPhoto())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(marker.isInfoWindowShown()){
                            marker.hideInfoWindow();
                            marker.showInfoWindow();
                        }
                        return false;
                    }
                }).into(image_shared);

        text_title.setText(mSharedLocation.getLocationTitle());
        text_description.setText(mSharedLocation.getLocationDescription());
        text_name.setText("by: "+mSharedLocation.getUser().getFirstname()+" "+mSharedLocation.getUser().getLastname());

        Long timeStamp = mSharedLocation.getDate().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String ConvertedDate = simpleDateFormat.format(new Date(timeStamp));
        text_date.setText(ConvertedDate);
    }

    // Getter and Setter Methods
    public Sharedlocation getmSharedLocation() {
        return mSharedLocation;
    }

    public void setmSharedLocation(Sharedlocation mSharedLocation) {
        this.mSharedLocation = mSharedLocation;
    }
}
