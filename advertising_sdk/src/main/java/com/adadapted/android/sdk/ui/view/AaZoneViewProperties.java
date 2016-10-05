package com.adadapted.android.sdk.ui.view;

/**
 * Created by chrisweeden on 4/28/16.
 */
public class AaZoneViewProperties {
    private final String zoneId;
    private final int resourceId;
    private final int backgroundColor;

    public AaZoneViewProperties(final String zoneId,
                                final int resourceId,
                                final int backgroundColor) {
        this.zoneId = zoneId;
        this.resourceId = resourceId;
        this.backgroundColor = backgroundColor;
    }

    public String getZoneId() {
        return zoneId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
