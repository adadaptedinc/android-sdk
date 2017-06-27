package com.adadapted.android.sdk.ui.view;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

class AaZoneViewControllerFactory {
    private static final String LOGTAG = AaZoneViewControllerFactory.class.getName();

    private static AaZoneViewControllerFactory sInstance;

    private final Map<String, AaZoneViewController> mZoneControllers;

    private AaZoneViewControllerFactory() {
        mZoneControllers = new HashMap<>();
    }

    static synchronized AaZoneViewController getController(final Context context,
                                                           final AaZoneViewProperties zoneProperties) {
        if(sInstance == null) {
            sInstance = new AaZoneViewControllerFactory();
        }

        final String zoneId = (zoneProperties == null) ? "" : zoneProperties.getZoneId();

        if(!sInstance.mZoneControllers.containsKey(zoneId)) {
            final AaZoneViewController controller = new AaZoneViewController(context.getApplicationContext(), zoneProperties);
            sInstance.mZoneControllers.put(zoneId, controller);
        }

        return sInstance.mZoneControllers.get(zoneId);
    }
}
