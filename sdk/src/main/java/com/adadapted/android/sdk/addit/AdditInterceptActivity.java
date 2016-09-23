package com.adadapted.android.sdk.addit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.adadapted.android.sdk.core.event.model.AppEventSource;
import com.adadapted.android.sdk.ext.factory.AnomalyTrackerFactory;
import com.adadapted.android.sdk.ext.factory.AppEventTrackerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AdditInterceptActivity extends AppCompatActivity {
    private static final String LOGTAG = AdditInterceptActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppEventTrackerFactory.registerEvent(AppEventSource.SDK,"addit_app_opened", new HashMap<String, String>());

        final Intent additIntent = getIntent();
        final Uri uri = additIntent.getData();
        final String data = uri.getQueryParameter("data");
        final byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
        final String jsonString = new String(decodedData);

        try {
            final JSONObject jsonObject = new JSONObject(jsonString);
            final JSONArray detailListItems = jsonObject.getJSONArray("detailed-list-items");

            final JSONObject payload = new JSONObject();
            payload.put("add_to_list_items", detailListItems);

            final AdditContentPayload content = new AdditContentPayload(this, payload);
            AdditContentPublisher.getInstance().publishContent(content);
        }
        catch(JSONException ex) {
            Log.e(LOGTAG, "Problem parsing Addit JSON input. Redirecting to launcher.");
            AnomalyTrackerFactory.registerAnomaly("",
                    "{\"raw\":\""+data+"\", \"parsed\":\""+jsonString+"\"}",
                    "ADDIT_PAYLOAD_PARSE_FAILED",
                    "Problem parsing Addit JSON input");

            final PackageManager pm = getPackageManager();
            final Intent mainActivityIntent =pm.getLaunchIntentForPackage(getPackageName());

            startActivity(mainActivityIntent);
        }
    }
}
