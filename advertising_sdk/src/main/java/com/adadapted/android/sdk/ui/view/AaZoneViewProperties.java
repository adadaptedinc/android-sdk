package com.adadapted.android.sdk.ui.view;

/**
 * Created by chrisweeden on 4/28/16.
 */
public class AaZoneViewProperties {
    private final String zoneId;
    private final int backgroundColor;

    public AaZoneViewProperties(final String zoneId,
                                final int backgroundColor) {
        this.zoneId = zoneId;
        this.backgroundColor = backgroundColor;
    }

    public String getZoneId() {
        return zoneId;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
