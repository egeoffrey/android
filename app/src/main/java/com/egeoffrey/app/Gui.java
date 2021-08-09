package com.egeoffrey.app;

import android.app.Activity;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler;

public class Gui {
    private Activity activity;
    private WebView webview;

    /** constructor */
    public Gui(Activity a) {
        activity = a;
        // create the webview
        webview = (WebView) activity.findViewById(R.id.webview);
        // set its settings
        WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setDomStorageEnabled(true);
        // create WebViewAssetLoader object
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .setDomain("egeoffrey.com")
                .addPathHandler("/assets/", new AssetsPathHandler(activity))
                .build();
        // set web client
        webview.setWebViewClient(new WebViewClient() {
            @Override
            @RequiresApi(21)
            public WebResourceResponse shouldInterceptRequest(WebView view,
                    WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
            @Override
            @SuppressWarnings("deprecation") // for API < 21
            public WebResourceResponse shouldInterceptRequest(WebView view,
                    String  url) {
                return assetLoader.shouldInterceptRequest(Uri.parse(url));
            }
        });
        // add Javascript interface
        webview.addJavascriptInterface(new AndroidInterface(activity), "Android");
    }

    /** load eGeoffrey UI */
    public void load() {
        webview.loadUrl("http://cloud.egeoffrey.com");
    }

    /** reload eGeoffrey UI */
    public void reload() {
        webview.reload();
    }

    /** go to the previous page */
    public void goBack() {
        if (webview.canGoBack()) webview.goBack();
    }

    /** // tell the webapp about the change */
    void setInForeground(boolean foreground) {
        webview.evaluateJavascript("javascript: window.EGEOFFREY_IN_FOREGROUND="+foreground, null);
    }
}
