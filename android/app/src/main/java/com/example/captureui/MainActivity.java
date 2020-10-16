package com.example.captureui;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static String[] optionsList = {"Chrome Custom Tabs", "Android WebView"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textVersion = findViewById(R.id.textVersion);
        textVersion.setText(getString(R.string.version_name, BuildConfig.VERSION_NAME));
    }

    /** Called when user clicks GO */
    public void openUrl(View view) {
        EditText editText = findViewById(R.id.urlEditText);
        String url = editText.getText().toString();
        boolean isURLValid = URLUtil.isValidUrl(url);
        if (!isURLValid)
        {
            Toast.makeText(getApplicationContext(), R.string.invalid_url_message, Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.option_select_title);
            alertDialogBuilder.setItems(optionsList, (dialog, which) -> {
                if (which == 0) {
                    openInCustomTabs();
                } else {
                    openInWebview();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    /** Called when user selects Chrome Custom Tabs */
    private void openInCustomTabs()
    {
        EditText editText = findViewById(R.id.urlEditText);
        String url = editText.getText().toString();

        CustomTabsIntent.Builder customTabsIntentBuilder = new CustomTabsIntent.Builder();
        customTabsIntentBuilder.setShowTitle(false);
        customTabsIntentBuilder.enableUrlBarHiding();
        CustomTabsIntent customTabsIntent = customTabsIntentBuilder.build();
        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, Uri.parse(url), new WebViewFallback());
    }

    /** Called when user selects WebView */
    private void openInWebview()
    {
        EditText editText = findViewById(R.id.urlEditText);
        String url = editText.getText().toString();

        WebViewFallback webViewFallback = new WebViewFallback();
        webViewFallback.openUri(this, Uri.parse(url));
    }
}
