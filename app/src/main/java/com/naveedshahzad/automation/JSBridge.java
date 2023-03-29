package com.naveedshahzad.automation;

import static com.naveedshahzad.automation.MainActivity.MY_BROADCAST_PACKAGE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JSBridge {
    private Context context;

    public JSBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    // Turn on/off airplane mode
    @JavascriptInterface
    public void setAirplaneMode(boolean isEnabled) {
        // Set the airplane mode on/off
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 1 : 0);
        } else {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, isEnabled ? 1 : 0);
        }
        // Broadcast an intent to inform other applications of the airplane mode change
        Intent intent = new Intent();
        intent.setAction(MY_BROADCAST_PACKAGE);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("state", isEnabled);
        context.sendBroadcast(intent);
    }

    @JavascriptInterface
    public void clearHistory(){
        //((MainActivity) context).clearBrowsingData();
         showToast("History clear successfully.");
    }
}

