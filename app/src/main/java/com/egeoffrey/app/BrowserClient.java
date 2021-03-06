package com.egeoffrey.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;


public class BrowserClient extends android.webkit.WebViewClient {

    private Activity activity = null;

    public BrowserClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }

}
