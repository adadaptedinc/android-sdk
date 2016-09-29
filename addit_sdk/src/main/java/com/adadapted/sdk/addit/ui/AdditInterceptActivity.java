package com.adadapted.sdk.addit.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.adadapted.sdk.addit.core.app.AppEventSource;
import com.adadapted.sdk.addit.ext.factory.AppErrorTrackingManager;
import com.adadapted.sdk.addit.ext.factory.AppEventTrackingManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        final Intent additIntent = getIntent();
        final Uri uri = additIntent.getData();

        final Map<String, String> params = new HashMap<>();
        params.put("url", uri.toString());
        AppEventTrackingManager.registerEvent(
                AppEventSource.SDK,
                "deeplink_url_received",
                params);

        final String data = uri.getQueryParameter("data");
        final byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
        final String jsonString = new String(decodedData);

        try {
            final JSONObject jsonObject = new JSONObject(jsonString);
            final JSONArray detailListItems = jsonObject.getJSONArray("detailed-list-items");

            final JSONObject payload = new JSONObject();
            payload.put("add_to_list_items", detailListItems);

            final AdditContent content = new AdditContent(this, payload);
            AdditContentPublisher.getInstance().publishContent(content);
        }
        catch(JSONException ex) {
            Log.e(LOGTAG, "Problem parsing Addit JSON input. Redirecting to launcher.");

            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("payload", "{\"raw\":\""+data+"\", \"parsed\":\""+jsonString+"\"}");
            errorParams.put("exception_message", ex.getMessage());
            AppErrorTrackingManager.registerEvent(
                    "ADDIT_PAYLOAD_PARSE_FAILED",
                    "Problem parsing Addit JSON input",
                    errorParams);

            final PackageManager pm = getPackageManager();
            final Intent mainActivityIntent = pm.getLaunchIntentForPackage(getPackageName());

            startActivity(mainActivityIntent);
        }
    }
}
