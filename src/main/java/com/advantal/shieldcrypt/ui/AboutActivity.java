package com.advantal.shieldcrypt.ui;

import static com.advantal.shieldcrypt.ui.XmppActivity.configureActionBar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.ui.util.SettingsUtils;
import com.advantal.shieldcrypt.utils.ThemeHelper;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onResume(){
        super.onResume();
        SettingsUtils.applyScreenshotPreventionSetting(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeHelper.find(this));

        setContentView(R.layout.activity_about);
        setSupportActionBar(findViewById(R.id.toolbar));
        configureActionBar(getSupportActionBar());
        setTitle(getString(R.string.title_activity_about_x, getString(R.string.app_name)));
    }
}
