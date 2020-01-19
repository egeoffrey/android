package com.egeoffrey.app;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/** Receive broadcasted messages and notify the user */
public class NotificationService extends BroadcastReceiver {
    private Context context;
    private int notificationCounter;
    private android.app.NotificationManager manager;
    private String channelId;
    private boolean inForeground = false;

    /** constructor, setup the notification channel */
    public NotificationService(Context c) {
        context = c;
        notificationCounter = 0;
        channelId = "Default";
        // get the notification manager
        manager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // create the channel if does not exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", android.app.NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

    /** receive a broadcasted message, notify the user */
    @Override
    public void onReceive(Context context, Intent intent) {
        // this is a notification
        if (intent.getAction() == "com.egeoffrey.NOTIFY") {
            // avoid notifying the user if the app is in foreground
            if (inForeground) return;
            // retrieve the relevant fields
            String title = intent.getStringExtra("title");
            String body = intent.getStringExtra("body");
            Long timestamp = intent.getLongExtra("timestamp", 0);
            // set the timestamp if not provided
            if (timestamp == 0) timestamp = Calendar.getInstance().getTimeInMillis();
            // create notification intent
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            // create notification builder
            Bitmap rawBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            NotificationCompat.Builder builder = new  NotificationCompat.Builder(context, channelId)
                    .setContentTitle(title.substring(0,1).toUpperCase() + title.substring(1))
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(body)))
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setLargeIcon(rawBitmap)
                    .setAutoCancel(true)
                    .setGroup("egeoffrey_notification_group")
                    .setContentIntent(pendingIntent)
                    .setWhen(timestamp);
            // notify
            notificationCounter++;
            manager.notify(notificationCounter, builder.build());
            // reset the counter if there are too many notifications
            if (notificationCounter > 30) notificationCounter = 0;
        }
        // the main app changed its status
        else if (intent.getAction() == "com.egeoffrey.IN_FOREGROUND") {
            // retrieve the relevant fields
            inForeground = intent.getBooleanExtra("inForeground", false);
        }
    }
}