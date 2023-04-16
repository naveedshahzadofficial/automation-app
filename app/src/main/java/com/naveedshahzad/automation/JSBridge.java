package com.naveedshahzad.automation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

    @JavascriptInterface
    public void setCompleted(){
        showToast("Your task is processing.");
        //((MainActivity) context).startWork();
        Intent intent = new Intent();
        intent.setAction(Constants.MY_BROADCAST_TASKS);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("count", 1);
        context.sendBroadcast(intent);
    }

    @JavascriptInterface
    public void verifyHuman(){
        ((MainActivity) context).simulateTouch(253,630);
    }
}

