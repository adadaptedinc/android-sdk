package com.adadapted.sdk.addit.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.adadapted.sdk.addit.core.app.AppEventSource;
import com.adadapted.sdk.addit.core.content.AdditContent;
import com.adadapted.sdk.addit.core.content.ContentPayloadParser;
import com.adadapted.sdk.addit.ext.factory.AppErrorTrackingManager;
import com.adadapted.sdk.addit.ext.factory.AppEventTrackingManager;

import java.util.HashMap;
import java.util.Map;

public class AdditInterceptActivity extends AppCompatActivity {
    private static final String LOGTAG = AdditInterceptActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppEventTrackingManager.registerEvent(
                AppEventSource.SDK,
                "addit_app_opened",
                new HashMap<String, String>());

        try {
            final Intent additIntent = getIntent();
            final Uri uri = additIntent.getData();

            final ContentPayloadParser parser = new ContentPayloadParser(this);
            final AdditContent content = parser.parse(uri);

            AdditContentPublisher.getInstance().publishContent(content);
        }
        catch(Exception ex) {
            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("exception_message", ex.getMessage());
            AppErrorTrackingManager.registerEvent(
                    "ADDIT_DEEPLINK_HANDLING_ERROR",
                    "Problem handling deeplink",
                    errorParams);

            final PackageManager pm = getPackageManager();
            final Intent mainActivityIntent = pm.getLaunchIntentForPackage(getPackageName());

            startActivity(mainActivityIntent);
        }
    }
}