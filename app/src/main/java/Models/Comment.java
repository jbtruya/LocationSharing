package Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment implements Parcelable {

    private String commentDocId;
    private Sharedlocation sharedLocation;
    private User user;
    private String comment;
    private @ServerTimestamp Date timeStamp;

    public Comment() {

    }

    public Comment(String commentDocId, Sharedlocation sharedLocation, User user, String comment, Date timeStamp) {
        this.commentDocId = commentDocId;
        this.sharedLocation = sharedLocation;
        this.user = user;
        this.comment = comment;
        this.timeStamp = timeStamp;
    }

    protected Comment(Parcel in) {
        commentDocId = in.readString();
        sharedLocation = in.readParcelable(Sharedlocation.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
        comment = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getCommentDocId() {
        return commentDocId;
    }

    public void setCommentDocId(String commentDocId) {
        this.commentDocId = commentDocId;
    }

    public Sharedlocation getSharedLocation() {
        return sharedLocation;
    }

    public void setSharedLocation(Sharedlocation sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentDocId);
        dest.writeParcelable(sharedLocation, flags);
        dest.writeParcelable(user, flags);
        dest.writeString(comment);
    }
}
