package com.adadapted.android.sdk.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.adadapted.android.sdk.core.addit.AdditContent;
import com.adadapted.android.sdk.core.addit.AdditContentPayloadParser;
import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.adadapted.android.sdk.ext.management.AppEventTrackingManager;
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher;

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

            final AdditContentPayloadParser parser = new AdditContentPayloadParser(this);
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
