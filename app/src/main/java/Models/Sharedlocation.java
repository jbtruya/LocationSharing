package Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Sharedlocation implements Parcelable {

    private String documentId;
    private User user;
    private String locationTitle;
    private String locationDescription;
    private String locationPhoto;
    private GeoPoint geoPoint;
    private @ServerTimestamp Date timeStamp;

    public static final Creator<Sharedlocation> CREATOR = new Creator<Sharedlocation>() {
        @Override
        public Sharedlocation createFromParcel(Parcel in) {
            return new Sharedlocation(in);
        }

        @Override
        public Sharedlocation[] newArray(int size) {
            return new Sharedlocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeString(locationTitle);
        dest.writeString(locationDescription);
        dest.writeString(locationPhoto);
    }

    public Sharedlocation() {
    }

    public Sharedlocation(String documentId, User user, String locationTitle, String locationDescription, String locationPhoto, GeoPoint geoPoint, Date timeStamp) {
        this.documentId = documentId;
        this.user = user;
        this.locationTitle = locationTitle;
        this.locationDescription = locationDescription;
        this.locationPhoto = locationPhoto;
        this.geoPoint = geoPoint;
        this.timeStamp = timeStamp;
    }

    protected Sharedlocation(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        locationTitle = in.readString();
        locationDescription = in.readString();
        locationPhoto = in.readString();
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getLocationPhoto() {
        return locationPhoto;
    }

    public void setLocationPhoto(String locationPhoto) {
        this.locationPhoto = locationPhoto;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}
