package com.example.captureui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "extra.url";
    public static final String BASE_URL = "https://capture.kyc.idfystaging.com/";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Objects.requireNonNull(getSupportActionBar()).hide();
        String url = getIntent().getStringExtra(EXTRA_URL);
        final WebView webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        //if SDK version is greater of 19 then activate hardware acceleration otherwise activate software acceleration
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 16) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WebView.setWebContentsDebuggingEnabled(true);
            int hasCameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);

            List<String> permissions = new ArrayList<>();

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.RECORD_AUDIO);
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 111);
            }
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webView.loadUrl(url);

        webView.setWebChromeClient(new WebChromeClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            // Grant permissions for cam
            @Override
            public void onPermissionRequest(final PermissionRequest request) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                if (ContextCompat.checkSelfPermission(WebViewActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(WebViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(WebViewActivity.this)
                                .setMessage("Allow Location Access?")
                                .setNeutralButton(R.string.alert_positive_button, (dialog, which) -> ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111)).show();
                    } else {
                        ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
                    }
                } else {
                    callback.invoke(origin, true, true);
                }
            }

            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivity(intent);
                } catch (ActivityNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView a_webview, String a_url, Bitmap favIcon) {
                super.onPageStarted(a_webview, a_url, favIcon);
                if (!a_url.contains(BASE_URL)) {
                    Log.d("onPageStarted", "URL: " + a_url);
                    finish();
                }
            }

            @Override
            public void onPageFinished(WebView a_webview, String a_url) {
                super.onPageFinished(a_webview, a_url);
                Log.d("onPageFinished", "URL: " + a_url);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
