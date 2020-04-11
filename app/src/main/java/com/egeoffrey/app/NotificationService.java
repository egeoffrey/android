package com.egeoffrey.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

/** Receive messages from the Firebase Cloud Service */
public class NotificationService extends FirebaseMessagingService {
    private android.app.NotificationManager manager;
    private String channelId = "Default";
    private boolean inForeground = false;

    /** constructor */
    public NotificationService() {
    }

    /** handle messages received from Firebase */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // get the notification manager
        manager = (android.app.NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        // create the notification channel if does not exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", android.app.NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        // ensure the message contains all required fields and is a notification
        String type = remoteMessage.getData().get("type");
        String house = remoteMessage.getData().get("title");
        house = house.substring(0,1).toUpperCase() + house.substring(1);
        int house_hash = house.hashCode();
        String body = remoteMessage.getData().get("body");
        Long timestamp = remoteMessage.getSentTime();
        if (type == null || house == null || body == null) return;
        if (! type.equals("notification")) return;
        // avoid notifying the user if the app is in foreground
        if (inForeground) return;
        // set the timestamp if not provided
        if (timestamp == 0) timestamp = Calendar.getInstance().getTimeInMillis();

        // check if there is already a notification showing up, if so keep track of them
        CharSequence[] previous_notifications = null;
        if (Build.VERSION.SDK_INT >= 23) {
            StatusBarNotification[] activeNotifications = manager.getActiveNotifications();
            // for each existing notification
            for (StatusBarNotification activeNotification : activeNotifications) {
                // skip if it belonging to a different house
                if (activeNotification.getNotification().extras.getString("android.title").hashCode() != house_hash) continue;
                // keep track of all the lines of the previous notification
                previous_notifications = activeNotification.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            }
        }

        // create notification intent
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // get icon
        Bitmap rawBitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);
        // build an inbox-style notification
        NotificationCompat.InboxStyle notificationStyle = new NotificationCompat.InboxStyle();
        int notificationCount = 1;
        // if there are other notifications already showing up, add them to the new notification
        if(previous_notifications != null && previous_notifications.length > 0) {
            for (CharSequence msg : previous_notifications) {
                if (!TextUtils.isEmpty(msg)) {
                    notificationStyle.addLine(msg.toString());
                }
            }
            notificationCount += previous_notifications.length;
            // add a counter of the notification
            notificationStyle.setSummaryText("("+notificationCount+")");
        }
        // append the latest notification
        notificationStyle.addLine(body);
        // create the notification builder
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                .setContentTitle(house)
                .setContentText(body)
                .setStyle(notificationStyle)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLargeIcon(rawBitmap)
                .setAutoCancel(true)
                .setGroup("egeoffrey_notification_group")
                .setContentIntent(pendingIntent)
                .setWhen(timestamp)
                .setNumber(notificationCount);

        // notify
        manager.notify(house_hash, builder.build());
    }
}
