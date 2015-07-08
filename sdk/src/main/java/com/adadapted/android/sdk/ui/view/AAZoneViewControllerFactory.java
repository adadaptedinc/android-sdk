package com.adadapted.android.sdk.ui.view;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 6/25/15.
 */
class AaZoneViewControllerFactory {
    private static AaZoneViewControllerFactory instance;

    public static synchronized AaZoneViewControllerFactory getInstance(final Context context) {
        if(instance == null) {
            instance = new AaZoneViewControllerFactory(context);
        }

        return instance;
    }

    private final Context context;
    private final Map<String, AaZoneViewController> zoneControllers;

    public AaZoneViewControllerFactory(final Context context) {
        this.context = context;
        this.zoneControllers = new HashMap<>();
    }

    public AaZoneViewController getController(final String zoneId,
                                              final int resourceId) {
        if(!zoneControllers.containsKey(zoneId)) {
            AaZoneViewController controller = new AaZoneViewController(context, zoneId, resourceId);
            zoneControllers.put(zoneId, controller);
        }

        return zoneControllers.get(zoneId);
    }
}
