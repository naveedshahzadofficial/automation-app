package com.naveedshahzad.automation;

import static com.naveedshahzad.automation.MainActivity.MY_BROADCAST_PACKAGE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
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

    @JavascriptInterface
    public void setCompleted(){
        showToast("Your task is completed.");
        setCounting();
    }

    @JavascriptInterface
    public void setCounting(){
        ((MainActivity) context).setCounting();
    }
}

