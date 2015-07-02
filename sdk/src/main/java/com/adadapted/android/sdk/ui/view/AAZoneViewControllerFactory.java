package com.adadapted.android.sdk.ui.view;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 6/25/15.
 */
public class AAZoneViewControllerFactory {
    private static AAZoneViewControllerFactory instance;

    public static synchronized AAZoneViewControllerFactory getInstance(final Context context) {
        if(instance == null) {
            instance = new AAZoneViewControllerFactory(context);
        }

        return instance;
    }

    private final Context context;
    private final Map<String, AAZoneViewController> zoneControllers;

    public AAZoneViewControllerFactory(final Context context) {
        this.context = context;
        this.zoneControllers = new HashMap<>();
    }

    public AAZoneViewController getController(final String zoneId,
                                              final int resourceId) {
        if(!zoneControllers.containsKey(zoneId)) {
            AAZoneViewController controller = new AAZoneViewController(context, zoneId, resourceId);
            zoneControllers.put(zoneId, controller);
        }

        return zoneControllers.get(zoneId);
    }
}
