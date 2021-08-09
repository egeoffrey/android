package com.egeoffrey.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
    private Gui gui = null;
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

        // initialize the gui
        gui = new Gui(this);

        // retrieve notification token
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) return;
                notificationToken = task.getResult().getToken();
            }
        });

        // load the gui
        gui.load();
        gui.setInForeground(true);
    }

    /** keep track when the app is paused or resumed */
    @Override
    protected void onPause() {
        super.onPause();
        gui.setInForeground(false);
    }

    /** keep track when the app is paused or resumed */
    @Override
    protected void onResume() {
        super.onResume();
        gui.setInForeground(true);
    }

    /** map back button with back in webview */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            gui.goBack();
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
        else if (id == R.id.reload_gui) {
            gui.reload();
            return true;
        }
        else if (id == R.id.getting_started) {
            showDialog(this, "Getting Started", "To fully enjoy a mobile experience with eGeoffrey, you would need:<br><br>" +
                    "1) an eGeoffrey instance installed and running somewhere;<br>"+
                    "2) the local eGeoffrey gateway reachable or the eGeoffrey Cloud Gateway configured;<br>"+
                    "3) [OPTIONAL] the package egeoffrey-notification-mobile installed and configured in your eGeoffrey instance with the device token you can get from the 'About' menu item of this app;<br><br>"+
                    "Please visit <a href=\"https://docs.egeoffrey.com/configure/mobile\">https://docs.egeoffrey.com/configure/mobile</a> for more information.");
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
