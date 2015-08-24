package com.adadapted.android.sdk.ui.view;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 6/25/15.
 */
class AaZoneViewControllerFactory {
    private static AaZoneViewControllerFactory sInstance;

    private final Map<String, AaZoneViewController> mZoneControllers;

    public AaZoneViewControllerFactory() {
        mZoneControllers = new HashMap<>();
    }

    public static AaZoneViewController getController(final Context context,
                                                     final String zoneId,
                                                     final int resourceId) {
        if(sInstance == null) {
            sInstance = new AaZoneViewControllerFactory();
        }

        if(!sInstance.mZoneControllers.containsKey(zoneId)) {
            AaZoneViewController controller = new AaZoneViewController(context, zoneId, resourceId);
            sInstance.mZoneControllers.put(zoneId, controller);
        }

        return sInstance.mZoneControllers.get(zoneId);
    }
}
