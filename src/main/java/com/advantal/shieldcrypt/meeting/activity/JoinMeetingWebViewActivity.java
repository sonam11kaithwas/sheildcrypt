package com.advantal.shieldcrypt.meeting.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.advantal.shieldcrypt.R;

import java.security.PublicKey;

public class JoinMeetingWebViewActivity extends AppCompatActivity {

    private WebView webPdf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webPdf = (WebView) findViewById(R.id.webPdf);
        WebSettings webSettings = webPdf.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webPdf.loadUrl("https://meet.jit.si/test123");
        webPdf.loadUrl("https://92.204.128.15:7443/ofmeet");

    }
}
