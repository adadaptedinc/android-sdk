package com.adadapted.sdk.addit.ui;

import android.app.Activity;

import com.adadapted.sdk.addit.core.app.AppEventSource;
import com.adadapted.sdk.addit.core.content.Content;
import com.adadapted.sdk.addit.ext.factory.AnomalyTrackingManager;
import com.adadapted.sdk.addit.ext.factory.AppEventTrackingManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class AdditContent implements Content {
    private final Activity activity;
    private final JSONObject payload;

    public AdditContent(final Activity activity,
                        final JSONObject payload) {
        this.activity = activity;
        this.payload = payload;
    }

    @Override
    public void acknowledge() {

        try {
            final JSONArray listItems = getPayload().getJSONArray("add_to_list_items");
            int itemCount = listItems.length();
            for (int i = 0; i < itemCount; i++) {
                final JSONObject item = (JSONObject) listItems.get(i);

                final Map<String, String> eventParams = new HashMap<>();
                eventParams.put("tracking_id", item.getString("tracking_id"));
                eventParams.put("item_name", item.getString("product_title"));

                AppEventTrackingManager.registerEvent(
                        AppEventSource.SDK,
                        "addit_added_to_list",
                        eventParams);
            }
        }
        catch(JSONException ex) {
            AnomalyTrackingManager.registerAnomaly(
                    getPayload().toString(),
                    "ADDIT_ADDED_TO_LIST_FAILED",
                    "Failed to parse Addit payload for processing.");
        }
    }

    @Override
    public int getType() {
        return 0;
    }

    public Activity getActivity() {
        return activity;
    }

    @Override
    public JSONObject getPayload() {
        return payload;
    }
}
