package com.adadapted.android.sdk.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.adadapted.android.sdk.core.addit.Content;
import com.adadapted.android.sdk.core.addit.deeplink.DeeplinkContentParser;
import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;
import com.adadapted.android.sdk.ext.management.AppEventTrackingManager;
import com.adadapted.android.sdk.ext.management.PayloadPickupManager;
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher;

import java.util.HashMap;
import java.util.Map;

public class AdditInterceptActivity extends AppCompatActivity {
    private static final String LOGTAG = AdditInterceptActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOGTAG, "Addit Intercept Activity Launched.");

        PayloadPickupManager.deeplinkInProgress();

        AppEventTrackingManager.registerEvent(
                AppEventSource.SDK,
                "addit_app_opened",
                new HashMap<String, String>());

        try {
            final Intent additIntent = getIntent();
            final Uri uri = additIntent.getData();

            final DeeplinkContentParser parser = new DeeplinkContentParser();
            final Content content = parser.parse(uri);

            Log.i(LOGTAG, "Addit content dispactched to App.");

            AdditContentPublisher.getInstance().publishContent(content);
        }
        catch(Exception ex) {
            Log.w(LOGTAG, "Problem dealing with Addit content. Recovering. " + ex.getMessage());

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

        PayloadPickupManager.deeplinkCompleted();

        finish();
    }
}
