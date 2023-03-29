package com.naveedshahzad.automation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_CODE = 100;
    static final String MY_BROADCAST_PACKAGE = "com.naveedshahzad.MyBroadcastMessage";
    private EditText etLink;
    private EditText etCount;
    private Button btStart;
    private Context context;
    private WebView wvChrome;

    private Resources resources;
    private InputStream inputStream;

    private String commonJsFile;
    private boolean inProgress = false;
    private int counting = 1;

    private LinearLayout llForm;


    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        resources = getResources();
        inputStream = resources.openRawResource(R.raw.common);

        etLink = findViewById(R.id.etLink);
        etCount = findViewById(R.id.etCount);
        btStart = findViewById(R.id.btStart);
        wvChrome = findViewById(R.id.wvChrome);
        llForm = findViewById(R.id.llForm);

        WebSettings webSettings = wvChrome.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        wvChrome.addJavascriptInterface(new JSBridge(this.context), "JSBridge");
        wvChrome.setWebChromeClient(new WebChromeClient());

        try {
            readJsFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        wvChrome.evaluateJavascript(this.commonJsFile, null);

        wvChrome.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(context, "onPageFinished", Toast.LENGTH_LONG).show();
                injectJavaScript(view);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                Toast.makeText(context, "shouldOverrideUrlLoading", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inProgress) {
                    // Stop the work if it's in progress
                    stopWork();
                    inProgress = false;
                } else {
                    llForm.setVisibility(View.GONE);
                    // Start the work if it's not in progress
                    startWork();
                    inProgress = true;
                }
            }
        });

        if(Settings.System.canWrite(this)){

        }else{
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:"+this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        requestPermissions();
    }


    private void readJsFile() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        commonJsFile = stringBuilder.toString();

    }

    private void injectJavaScript(WebView view){
        String javaScript ="javascript:"+ this.commonJsFile;
        //view.loadUrl(javaScript);
        view.evaluateJavascript(this.commonJsFile, null);
    }

    protected void setAirplaneMode(boolean isEnabled) {
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

    @AfterPermissionGranted(MY_PERMISSIONS_REQUEST_CODE)
    private void requestPermissions()
    {
        String[] perms = {Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_WIFI_STATE};
        if(EasyPermissions.hasPermissions(this, perms)){
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }else{
            EasyPermissions.requestPermissions(this, "We need this permissions to run your app", MY_PERMISSIONS_REQUEST_CODE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permission has been granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }else{
            requestPermissions();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(MY_BROADCAST_PACKAGE);
        registerReceiver(airplaneModeChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangeReceiver);
    }

    public void clearBrowsingData(){
        wvChrome.clearCache(true);
        wvChrome.clearFormData();
        wvChrome.clearHistory();
    }

    private void startWork() {
        int total = Integer.parseInt(etCount.getText().toString());
        if(total >= counting) {
            clearBrowsingData();
            btStart.setText("In Process (" + counting + ")");
            wvChrome.loadUrl(etLink.getText().toString());
        }
    }

    private void stopWork() {
        btStart.setText("START");
    }

    public void setCounting(){
        counting++;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item:
                if (llForm.getVisibility() == View.GONE) {
                    llForm.setVisibility(View.VISIBLE);
                } else {
                    llForm.setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}