package com.egeoffrey.app;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/** Receive messages from the Firebase Cloud Service */
public class MessagingService extends FirebaseMessagingService {

    /** handle messages received from Firebase */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // ensure the message contains all required fields and is a notification
        String type = remoteMessage.getData().get("type");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        if (type == null || title == null || body == null) return;
        if (! type.equals("notification")) return;
        // dispatch the notification to the notification service
        Intent notification = new Intent("com.egeoffrey.NOTIFY");
        notification.putExtra("title", remoteMessage.getData().get("title"));
        notification.putExtra("body", remoteMessage.getData().get("body"));
        notification.putExtra("timestamp", remoteMessage.getSentTime());
        sendBroadcast(notification);
    }

}
