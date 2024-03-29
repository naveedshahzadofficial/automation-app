package com.naveedshahzad.automation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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

    public RelativeLayout rlWebView;
    public WebView wvChrome;

    public ProgressBar pbWebView;

    private Resources resources;
    private InputStream inputStream;

    private String commonJsFile;
    private boolean inProgress = false;
    private int counting = 1;

    private LinearLayout llForm;
    private String saveWebsiteLink;

    ActivityResultLauncher<String> requestPermissionLauncher;


    SharedPreferencesManager spm;
    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();

    TaskBroadcastReceiver tbr = new TaskBroadcastReceiver();

    CookieManager cookieManager = CookieManager.getInstance();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

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
        rlWebView = findViewById(R.id.rlWebView);

        pbWebView = findViewById(R.id.pbWebView);
        llForm = findViewById(R.id.llForm);
        spm = new SharedPreferencesManager(this);

        /* Shared Preference Manage*/
        saveWebsiteLink = spm.getString(Constants.WEBSITE_URL, "");
        if(saveWebsiteLink.equals("")){
            spm.saveString(Constants.WEBSITE_URL, etLink.getText().toString());
        }else{
            etLink.setText(saveWebsiteLink);
        }

        try {
            readJsFile();

        } catch (IOException e) {
            e.printStackTrace();
        }



        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!saveWebsiteLink.equals(etLink.getText().toString())){
                    spm.saveString(Constants.WEBSITE_URL, etLink.getText().toString());
                    saveWebsiteLink = etLink.getText().toString();
                }

               if (inProgress) {
                    stopWork();
                    inProgress = false;
                } else {
                    llForm.setVisibility(View.GONE);
                    rlWebView.setVisibility(View.VISIBLE);
                    startWork();
                    inProgress = true;
                }
            }
        });
        requestPermissions();
        securePermission();
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
            try {
                Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 1 : 0);
                executeCommandWithoutWait(Constants.COMMAND_FLIGHT_MODE_BROADCAST+ isEnabled);
                //showToast(isEnabled?"AirPlane mode is on":"AirPlane mode is off");
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
        } else {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, isEnabled ? 1 : 0);
            // Broadcast an intent to inform other applications of the airplane mode change
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("state", isEnabled);
            sendBroadcast(intent);
        }
    }

    protected void setMobileData(boolean isEnabled) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        cm.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                showToast("network connection is available");
            }

            @Override
            public void onLost(Network network) {
                showToast("network connection is lost");
            }
        });

    }

    @AfterPermissionGranted(MY_PERMISSIONS_REQUEST_CODE)
    private void requestPermissions()
    {
        writeSettingPermission();
        String[] perms = {Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.WAKE_LOCK};
        if(!EasyPermissions.hasPermissions(this, perms)){
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
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeChangeReceiver, filter);

        IntentFilter filter2 = new IntentFilter(Constants.MY_BROADCAST_TASKS);
        registerReceiver(tbr, filter2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangeReceiver);
        unregisterReceiver(tbr);
        clearBrowsingData();
    }

    public void clearBrowsingData(){
        wvChrome.stopLoading();
        wvChrome.clearHistory();
        wvChrome.clearCache(true);
        wvChrome.clearFormData();
        wvChrome.destroy();
        rlWebView.removeView(wvChrome);
        wvChrome = null;
        deleteDataCmd();
    }

    public void startWork() {
        int total = Integer.parseInt(etCount.getText().toString());
        if(total >= counting) {
            btStart.setText("In Process (" + counting + ")");
            this.wvChromeInitialize();
            wvChrome.loadUrl(etLink.getText().toString());
        }else{
            showToast("Your Count has been completed.");
        }
    }

    private void stopWork() {
        wvChrome.stopLoading();
        btStart.setText("START");
        counting=1;
        this.clearBrowsingData();
    }

    public void setCounting()
    {
        this.counting++;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item:
                if (llForm.getVisibility() == View.GONE) {
                    llForm.setVisibility(View.VISIBLE);
                    rlWebView.setVisibility(View.GONE);
                } else {
                    llForm.setVisibility(View.GONE);
                    rlWebView.setVisibility(View.VISIBLE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void writeSettingPermission(){
        if(!Settings.System.canWrite(this)){
        // Define an ActivityResultContract for the intent you want to start
        ActivityResultContract<String, Boolean> requestPermissionContract =
                new ActivityResultContract<String, Boolean>() {
                    @NonNull
                    @Override
                    public Intent createIntent(@NonNull Context context, String input) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        return intent;
                    }

                    @Override
                    public Boolean parseResult(int resultCode, @Nullable Intent intent) {
                        return Settings.System.canWrite(context);
                    }
                };

        // Create an ActivityResultLauncher for the requestPermissionContract
        requestPermissionLauncher =
                registerForActivityResult(requestPermissionContract, isGranted -> {
                    if (isGranted) {
                        Toast.makeText(context, "Permission is granted.", Toast.LENGTH_LONG).show();
                        securePermission();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("The app needs the write settings permission to function properly. Would you like to grant the permission now?")
                                .setPositiveButton("Grant", (dialog, which) -> {
                                    // Launch the permission request again
                                    requestPermissionLauncher.launch("");
                                })
                                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                    }
                                })
                                .create()
                                .show();
                    }
                });

        // Start the activity using the requestPermissionLauncher


            requestPermissionLauncher.launch("");
        }
    }

    private void securePermission(){
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("pm grant " + getApplicationContext().getPackageName() + " android.permission.WRITE_SECURE_SETTINGS \n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (RuntimeException | IOException e) {
            Log.e(TAG, "Exception :( " + e.getMessage());
            showToast(e.getMessage());
        }
    }

    public void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public WebView getWebView(){
        return this.wvChrome;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void wvChromeInitialize(){
        wvChrome = new WebView(context);
        wvChrome.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rlWebView.addView(wvChrome);
        WebSettings webSettings = wvChrome.getSettings();
        //webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadsImagesAutomatically(true);
        wvChrome.clearHistory();
        wvChrome.clearFormData();
        wvChrome.clearCache(true);
        wvChrome.setScrollbarFadingEnabled(true);
        wvChrome.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        wvChrome.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wvChrome.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        //wvChrome.addJavascriptInterface(new JSBridge(this.context), "JSBridge");
        wvChrome.setWebChromeClient(new WebChromeClient());

        //wvChrome.evaluateJavascript(this.commonJsFile, null);

        wvChrome.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //injectJavaScript(view);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                //showToast(""+errorResponse.getStatusCode());
                /*if(errorResponse.getStatusCode()==404){
                    startWork();
                }*/
            }


        });

        cookieManager.removeAllCookies(null);
        cookieManager.removeSessionCookies(null);
        cookieManager.setAcceptCookie(true);

    }

    private void executeCommandWithoutWait(String command) {
        Log.d(TAG, "executeCommandWithoutWait");
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.d(TAG, command);
        } catch (IOException e) {
            Log.e(TAG, "su command has failed due to: " + e.fillInStackTrace());
        }
    }

    public void simulateTouch(int x, int y) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime, eventTime, action, x, y, metaState
        );
        dispatchTouchEvent(motionEvent);

        action = MotionEvent.ACTION_UP;
        motionEvent = MotionEvent.obtain(
                downTime, eventTime, action, x, y, metaState
        );
        dispatchTouchEvent(motionEvent);
    }

    public void deleteData(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.clearApplicationUserData();
    }

    public void deleteDataCmd() {
        String packageName = "com.naveedshahzad.automation"; // replace with the actual package name of the app
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }


}