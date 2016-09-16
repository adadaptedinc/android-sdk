package com.adadapted.android.sdk.addit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.adadapted.android.sdk.ext.factory.AppEventTrackerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class AdditInterceptActivity extends AppCompatActivity {
    private static final String LOGTAG = AdditInterceptActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppEventTrackerFactory.registerEvent("","addit_app_opened", new HashMap<String, String>());

        final Intent intent = getIntent();
        final String action = intent.getAction();
        final Uri uri = intent.getData();

        Log.i(LOGTAG, "Intent Action: " +action);
        Log.i(LOGTAG, "Intent URI: " + uri.toString());

        final String data = uri.getQueryParameter("data");
        Log.i(LOGTAG, "Base64 Data: " + data);
        final byte[] decodedData = Base64.decode(data, Base64.NO_WRAP);

        try {
            final String jsonString = new String(decodedData, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonString);
            Log.i(LOGTAG, "Parsed JSON String: " + jsonObject.toString());

            JSONArray detailListItems = jsonObject.getJSONArray("detailed-list-items");

            JSONObject payload = new JSONObject();
            payload.put("add_to_list_items", detailListItems);

            AdditContentPayload content = new AdditContentPayload(this, payload);
            AdditContentPublisher.getInstance().publishContent(content);
        }
        catch(UnsupportedEncodingException ex) {
            Log.e(LOGTAG, "Problem with UTF-8 Encoding Type", ex);
        }
        catch(JSONException ex) {
            Log.e(LOGTAG, "Problem parsing JSON input", ex);
        }
    }
}
