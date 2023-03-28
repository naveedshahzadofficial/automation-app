package com.naveedshahzad.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText etLink;
    private EditText etCount;
    private Button btStart;
    private Context context;
    private String deviceName;
    private WebView wvChrome;

    private Resources resources;
    private InputStream inputStream;

    private String commonJsFile;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        deviceName = Build.MODEL;

         resources = getResources();
         inputStream = resources.openRawResource(R.raw.common);

        etLink = findViewById(R.id.etLink);
        etCount = findViewById(R.id.etCount);
        btStart = findViewById(R.id.btStart);
        wvChrome = findViewById(R.id.wvChrome);


        WebSettings webSettings = wvChrome.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        wvChrome.setWebChromeClient(new WebChromeClient());

        wvChrome.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJavaScript(view);

            }
        });
        wvChrome.addJavascriptInterface(new JSBridge(this), "JSBridge");



        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Working Button", Toast.LENGTH_LONG).show();
                try {
                    readJsFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                wvChrome.loadUrl(etLink.getText().toString());
            }
        });
    }

    private void injectJavaScript(WebView view){
        String javaScript ="javascript:"+ commonJsFile;
        Log.d(TAG, "injectJavaScript: "+javaScript);
        //view.loadUrl(javaScript);
        view.evaluateJavascript(commonJsFile, null);

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

}