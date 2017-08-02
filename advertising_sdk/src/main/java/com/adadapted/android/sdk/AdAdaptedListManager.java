package com.adadapted.android.sdk;

import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.HashMap;
import java.util.Map;

public class AdAdaptedListManager {
    public static synchronized void itemAddedToList(String item) {
        if(item == null || item.isEmpty()) {
            return;
        }

        final Map<String, String> params = new HashMap<>();
        params.put("", item);

        AppEventClient.trackAppEvent("user_added_to_list", params);
    }

    public static synchronized void itemCrossedOffList(String item) {
        if(item == null || item.isEmpty()) {
            return;
        }

        final Map<String, String> params = new HashMap<>();
        params.put("", item);

        AppEventClient.trackAppEvent("user_crossed_off_list", params);
    }

    public static synchronized void itemDeletedFromList(String item) {
        if(item == null || item.isEmpty()) {
            return;
        }

        final Map<String, String> params = new HashMap<>();
        params.put("item_name", item);

        AppEventClient.trackAppEvent("user_deleted_from_list", params);
    }
}
