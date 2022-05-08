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
    private Date date;


    public Sharedlocation() {
    }

    protected Sharedlocation(Parcel in) {
        documentId = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        locationTitle = in.readString();
        locationDescription = in.readString();
        locationPhoto = in.readString();
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(documentId);
        parcel.writeParcelable(user, i);
        parcel.writeString(locationTitle);
        parcel.writeString(locationDescription);
        parcel.writeString(locationPhoto);
    }
}
