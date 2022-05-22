package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Message implements Parcelable {
    private String conversationDocId;
    private String senderId;
    private String message;
    private String photoUri;
    private Date date;

    public Message() {
    }

    public String getConversationDocId() {
        return conversationDocId;
    }

    public void setConversationDocId(String conversationDocId) {
        this.conversationDocId = conversationDocId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    protected Message(Parcel in) {
        conversationDocId = in.readString();
        senderId = in.readString();
        message = in.readString();
        photoUri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationDocId);
        dest.writeString(senderId);
        dest.writeString(message);
        dest.writeString(photoUri);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
