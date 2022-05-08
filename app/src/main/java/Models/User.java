package Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String uid;
    private String firstname;
    private String lastname;
    private String email;
    private String avatar;
    private String fcmToken;

    public User() {
    }

    public User(String uid, String firstname, String lastname, String email, String avatar) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.avatar = avatar;
    }

    protected User(Parcel in) {
        uid = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        email = in.readString();
        avatar = in.readString();
        fcmToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(firstname);
        parcel.writeString(lastname);
        parcel.writeString(email);
        parcel.writeString(avatar);
        parcel.writeString(fcmToken);
    }
}
