package com.egeoffrey.app;

import android.content.Context;
import android.webkit.JavascriptInterface;


public class AndroidInterface {
    private Context context;
    private NotificationHandler notificationHandler;

    /** Instantiate the interface and initialize the notification server */
    AndroidInterface(Context c) {
        context = c;
        notificationHandler = new NotificationHandler(context);
    }

    /** notify the user */
    @android.webkit.JavascriptInterface
    public void notify(String house_name, String severity, String message) {
        // notify the user only if in foreground (if in background Firebase will take care of it
        if (((MainActivity)context).inForeground) notificationHandler.notify(house_name, message);
    }
}