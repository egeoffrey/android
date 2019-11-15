package com.egeoffrey.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class WebAppInterface {
    Context context;
    int notification_counter = 0;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        context = c;
    }

    /** Show a toast from the web page */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @JavascriptInterface
    public void notify(String type, String message) {
        // prepare intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // get NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // build up the notification
        if (type == "success") type = "info";
        int color = Color.GRAY;
        if (type == "warning") color = Color.YELLOW;
        if (type == "error") color = Color.RED;

        // notify
        Notification notification = null;
        String type_uppercase = type.substring(0, 1).toUpperCase() + type.substring(1);
        if (Build.VERSION.SDK_INT >= 26) {
            notification = new NotificationCompat.Builder(context, ((MainActivity)context).notification_channel_id)
                    .setContentTitle(type_uppercase)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message))
                    .setColor(color)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        } else {
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle(type_uppercase)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            notification = builder.build();
        }
        notification_counter++;
        notificationManager.notify(notification_counter, notification);
        if (notification_counter > 10) notification_counter = 0;
    }
}