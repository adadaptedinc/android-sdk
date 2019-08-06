package com.adadapted.android.sdk;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;

import java.util.HashMap;
import java.util.Map;

public class AdAdaptedListManager {
    private static final String LOGTAG = AdAdaptedListManager.class.getName();

    private static final String LIST_NAME = "list_name";
    private static final String ITEM_NAME = "item_name";

    public static synchronized void itemAddedToList(final String item) {
        itemAddedToList("", item);
    }

    public static synchronized void itemAddedToList(final String list, final String item) {
        if(item == null || item.isEmpty()) {
            return;
        }

        final Map<String, String> params = new HashMap<>();
        params.put(LIST_NAME, list);
        params.put(ITEM_NAME, item);

        AppEventClient.trackAppEvent("user_added_to_list", params);

        Log.i(LOGTAG, String.format("%s was added to %s", item, list));
    }

    public static synchronized void itemCrossedOffList(final String item) {
        itemCrossedOffList("", item);
    }

    public static synchronized void itemCrossedOffList(final String list, final String item) {
        if(item == null || item.isEmpty()) {
            return;
        }

        final Map<String, String> params = new HashMap<>();
        params.put(LIST_NAME, list);
        params.put(ITEM_NAME, item);

        AppEventClient.trackAppEvent("user_crossed_off_list", params);

        Log.i(LOGTAG, String.format("%s was crossed off %s", item, list));
    }

    public static synchronized void itemDeletedFromList(final String item) {
        itemDeletedFromList("", item);
    }

    public static synchronized void itemDeletedFromList(final String list, final String item) {
        if(item == null || item.isEmpty()) {
            return;
        }

        final Map<String, String> params = new HashMap<>();
        params.put(LIST_NAME, list);
        params.put(ITEM_NAME, item);

        AppEventClient.trackAppEvent("user_deleted_from_list", params);

        Log.i(LOGTAG, String.format("%s was deleted from %s", item, list));
    }
}
