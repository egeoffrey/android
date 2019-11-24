package com.egeoffrey.app;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    private NotificationHandler notificationHandler;

    /** handle messages recevied from Firebase */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // initialize notification service if not done yet
        if (notificationHandler == null) notificationHandler = new NotificationHandler(this);
        // notify the user
        notificationHandler.notify(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }


}
