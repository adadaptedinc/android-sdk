package com.adadapted.android.sdk.addit;

import android.app.Activity;

import com.adadapted.android.sdk.core.content.ContentPayload;
import com.adadapted.android.sdk.ext.factory.AppEventTrackerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class AdditContentPayload implements ContentPayload {
    private final Activity activity;
    private final JSONObject payload;

    public AdditContentPayload(final Activity activity,
                               final JSONObject payload) {
        this.activity = activity;
        this.payload = payload;
    }

    @Override
    public void acknowledge() {

        try {
            JSONArray listItems = getPayload().getJSONArray("add_to_list_items");
            int itemCount = listItems.length();
            for (int i = 0; i < itemCount; i++) {
                JSONObject item = (JSONObject) listItems.get(i);

                Map<String, String> eventParams = new HashMap<>();
                eventParams.put("product_title", item.getString("product_title"));

                AppEventTrackerFactory.registerEvent(
                        item.getString("tracking_id"),
                        "addit_added_to_list",
                        eventParams);
            }
        }
        catch(JSONException ex) {

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
