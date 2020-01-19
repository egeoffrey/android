package com.egeoffrey.app;

import android.content.Context;
import android.webkit.JavascriptInterface;

/** Provide an interface to the webapp for calling native functions from there */
public class AndroidInterface {
    private Context context;

    /** Instantiate the interface and initialize the notification server */
    AndroidInterface(Context c) {
        context = c;
    }

    /** notify the user */
    @android.webkit.JavascriptInterface
    public void notify(String house_name, String severity, String message) {
        // do nothing, the notification service takes care of it regardless if in background or foreground
    }
}