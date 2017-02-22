package com.adadapted.android.sdk.ui.view;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 6/25/15
 */
class AaZoneViewControllerFactory {
    private static final String LOGTAG = AaZoneViewControllerFactory.class.getName();

    private static AaZoneViewControllerFactory sInstance;

    private final Map<String, AaZoneViewController> mZoneControllers;

    private AaZoneViewControllerFactory() {
        mZoneControllers = new HashMap<>();
    }

    public static AaZoneViewController getController(final Context context,
                                                     final AaZoneViewProperties zoneProperties) {
        if(sInstance == null) {
            sInstance = new AaZoneViewControllerFactory();
        }

        String zoneId = "";
        if(zoneProperties != null) {
            zoneId = zoneProperties.getZoneId();
        }

        if(!sInstance.mZoneControllers.containsKey(zoneId)) {
            //Log.d(LOGTAG, String.format("No controller found for Zone: %s. Creating one.", zoneId));
            AaZoneViewController controller = new AaZoneViewController(context, zoneProperties);
            sInstance.mZoneControllers.put(zoneId, controller);
        }

        return sInstance.mZoneControllers.get(zoneId);
    }
}
