package com.egeoffrey.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHandler {
    private Context context;
    private int notificationCounter;
    private android.app.NotificationManager manager;
    private String channelId;

    /** initialize the class */
    public NotificationHandler(Context c) {
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

    /** create the notification */
    public void notify(String title, String body) {
        // create intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                .setContentIntent(pendingIntent);
        // notify
        notificationCounter++;
        manager.notify(notificationCounter, builder.build());
        if (notificationCounter > 30) notificationCounter = 0;
    }
}
