package Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    String dataFrom;
    String userId;
    String documentId;
    int notificationId;
    String notificationDocumentId;
    String notificationTitle;
    String notificationBody;

    public Notification() {
    }

    protected Notification(Parcel in) {
        dataFrom = in.readString();
        userId = in.readString();
        documentId = in.readString();
        notificationId = in.readInt();
        notificationDocumentId = in.readString();
        notificationTitle = in.readString();
        notificationBody = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dataFrom);
        dest.writeString(userId);
        dest.writeString(documentId);
        dest.writeInt(notificationId);
        dest.writeString(notificationDocumentId);
        dest.writeString(notificationTitle);
        dest.writeString(notificationBody);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationDocumentId() {
        return notificationDocumentId;
    }

    public void setNotificationDocumentId(String notificationDocumentId) {
        this.notificationDocumentId = notificationDocumentId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }
}
