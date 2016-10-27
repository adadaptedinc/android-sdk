package com.adadapted.sdk.addit.core.content;

import android.app.Activity;

import com.adadapted.sdk.addit.core.app.AppEventSource;
import com.adadapted.sdk.addit.ext.factory.AppErrorTrackingManager;
import com.adadapted.sdk.addit.ext.factory.AppEventTrackingManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class AdditContent implements Content<List<AdditAddToListItem>> {
    private final Activity activity;
    private final int type;
    private final List<AdditAddToListItem> payload;

    public static final int ADD_TO_LIST_ITEMS = 1;
    public static final int ADD_TO_LIST_ITEM = 2;

    public AdditContent(final Activity activity,
                        final int type,
                        List<AdditAddToListItem> payload) {
        this.activity = activity;
        this.type = type;
        this.payload = payload;
    }

    @Override
    public void acknowledge() {
        for (AdditAddToListItem item : payload) {
            final Map<String, String> eventParams = new HashMap<>();
            eventParams.put("tracking_id", item.getTrackingId());
            eventParams.put("item_name", item.getTitle());

            AppEventTrackingManager.registerEvent(
                    AppEventSource.SDK,
                    "addit_added_to_list",
                    eventParams);
        }
    }

    @Override
    public void failed(final String message) {
        AppErrorTrackingManager.registerEvent(
                "ADDIT_CONTENT_FAILED",
                message,
                new HashMap<String, String>());
    }

    @Override
    public int getType() {
        return type;
    }

    public Activity getActivity() {
        return activity;
    }

    @Override
    public List<AdditAddToListItem> getPayload() {
        return payload;
    }
}
