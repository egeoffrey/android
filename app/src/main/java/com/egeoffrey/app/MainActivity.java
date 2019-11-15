package com.egeoffrey.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private WebView webview = null;
    public String notification_channel_id = "egeoffrey_notification";
    public CharSequence notification_channel_name = "Notifications";
    public String version = "1.0-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create webview
        this.webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        webview.setWebViewClient(webViewClient);

        // create Javascript interface
        webview.addJavascriptInterface(new WebAppInterface(this), "Android");

        // setup notifications
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(notification_channel_id, notification_channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{1000, 2000});
            NotificationManager notification_manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notification_manager.createNotificationChannel(notificationChannel);
        }

        // load eGeoffrey app
        webview.loadUrl("file:///android_asset/html/index.html");
    }

    // map back button with back in webview
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webview.canGoBack()) {
            this.webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            showDialog(this, "About", "eGeoffrey App version: "+VersionName(this));
            return true;
        }
        else if (id == R.id.getting_started) {
            showDialog(this, "Getting Started", "To use this app you would need:<br><br>" +
                    "1) an eGeoffrey instance installed and running somewhere;<br>"+
                    "2) the eGeoffrey gateway (default port: 443);<br>"+
                    "3) valid credentials set up for your house;<br><br>"+
                    "To <b>receive notifications</b> even when the app is in background, disable <i>Battery Optimization</i> for this app in the Android's settings menu.<br><br>"+
                    "Please visit <a href=\"https://www.egeoffrey.com\">https://www.egeoffrey.com</a> for more information.");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showDialog(Context c, String title, String text) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(Html.fromHtml(text))
                .setNegativeButton("Ok", null)
                .create();
        dialog.show();
    }

    static String VersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }

}
