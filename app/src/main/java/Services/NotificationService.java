package Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.locationsharing.MainActivity;
import com.example.locationsharing.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class NotificationService extends FirebaseMessagingService {
    private String  dataFrom;
    private int notificationId;
    private String ChannelId = "NotificationChannel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String , String> data = message.getData();
        dataFrom =data.get("dataFrom");
        notificationId = Integer.parseInt(Objects.requireNonNull(data.get("notificationId")));
        if(dataFrom != null){
            switch (dataFrom){
                case "NewComment":
                    String documentId = data.get("documentId");
                    NewCommentNotification(message.getNotification().getTitle(), message.getNotification().getBody(), documentId,notificationId);
                    break;
                case "MessageNotification":
                    String conversationId = data.get("conversationId");
                    MessageNotification(message.getNotification().getTitle(),message.getNotification().getBody(), conversationId, notificationId);
                    break;
            }
        }
    }

    public void NewCommentNotification(String title, String body, String documentId,int notificationId){
        NotificationManager manager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(ChannelId,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Notifications Channel");
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dataFrom", "NewComment");
        intent.putExtra("documentId", documentId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), ChannelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_icon_map)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .build();

        manager.notify(notificationId, notification);
    }
    public void MessageNotification(String title, String body, String conversationId,int notificationId){
        NotificationManager manager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(ChannelId,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Notifications Channel");
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dataFrom", "MessageNotification");
        intent.putExtra("conversationId", conversationId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, intent,PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), ChannelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_icon_map)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .build();

        manager.notify(notificationId, notification);
    }
}
