package com.egeoffrey.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


/** Main App, when the app is run, onCreate() is called */
public class MainActivity extends AppCompatActivity {
    private WebView browser = null;
    private String notificationToken = "Unknown";
    private NotificationService notificationService;

    /** initialize the app */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup notification service
        notificationService = new NotificationService();

        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create webview
        this.browser = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        BrowserClient browserClient = new BrowserClient(this);
        browser.setWebViewClient(browserClient);

        // add Javascript interface
        browser.addJavascriptInterface(new AndroidInterface(this), "Android");

        // retrieve notification token
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) return;
                notificationToken = task.getResult().getToken();
            }
        });

        // load eGeoffrey UI
        browser.loadUrl("file:///android_asset/html/index.html");
        setInForeground(true);
    }

    void setInForeground(boolean foreground) {
        // tell the webapp about the change
        browser.evaluateJavascript("javascript: window.EGEOFFREY_IN_FOREGROUND="+foreground, null);
    }

    /** keep track when the app is paused or resumed */
    @Override
    protected void onPause() {
        super.onPause();
        setInForeground(false);
    }

    /** keep track when the app is paused or resumed */
    @Override
    protected void onResume() {
        super.onResume();
        setInForeground(true);
    }

    /** map back button with back in webview */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.browser.canGoBack()) {
            this.browser.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /** what to do when the menu is opened */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /** what to do when a menu item is selected */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            String version;
            try {
                version = this.getPackageManager().getPackageInfo(this.getPackageName(),0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                version = "Unknown";
            }
            showDialog(this, "About", "eGeoffrey App v"+version+"<br><br>"+
                    "Device Token: "+ notificationToken);
            return true;
        }
        else if (id == R.id.getting_started) {
            showDialog(this, "Getting Started", "To fully enjoy a mobile experience with eGeoffrey, you would need:<br><br>" +
                    "1) an eGeoffrey instance installed and running somewhere;<br>"+
                    "2) the eGeoffrey gateway reachable from the network this device is connected to;<br>"+
                    "3) valid credentials set up for your house on the gateway;<br>"+
                    "4) the package 'egeoffrey-notification-mobile' installed in your eGeoffrey instance to receive notifications;<br>"+
                    "5) the 'notification/mobile' module configured with the device token you can get from the 'About' menu item of this app;<br><br>"+
                    "Please visit <a href=\"https://www.egeoffrey.com\">https://www.egeoffrey.com</a> for more information.");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** show a popup with some text */
    private void showDialog(Context c, String title, String text) {
        TextView showText = new TextView(this);
        showText.setText(Html.fromHtml(text));
        showText.setTextIsSelectable(true);
        showText.setPadding(50,50,50,50);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setView(showText)
                .setTitle(title)
                .setCancelable(true)
                .setNegativeButton("Ok", null)
                .create();
        dialog.show();
    }

}
