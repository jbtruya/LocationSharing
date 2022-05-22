package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Conversation implements Parcelable {
    private String conversationDocId;
    private String conversationStatus;
    private String latestMessage;
    private ArrayList<String> userId;

    public Conversation() {
    }

    public String getConversationDocId() {
        return conversationDocId;
    }

    public void setConversationDocId(String conversationDocId) {
        this.conversationDocId = conversationDocId;
    }

    public String getConversationStatus() {
        return conversationStatus;
    }

    public void setConversationStatus(String conversationStatus) {
        this.conversationStatus = conversationStatus;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public ArrayList<String> getUserId() {
        return userId;
    }

    public void setUserId(ArrayList<String> userId) {
        this.userId = userId;
    }

    protected Conversation(Parcel in) {
        conversationDocId = in.readString();
        conversationStatus = in.readString();
        latestMessage = in.readString();
        userId = in.createStringArrayList();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(conversationDocId);
        parcel.writeString(conversationStatus);
        parcel.writeString(latestMessage);
        parcel.writeStringList(userId);
    }
}
