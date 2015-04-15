package com.adadapted.android.sdk.core.zone;


import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.common.Dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 3/26/15.
 */
public class Zone {
    private static final String TAG = Zone.class.getName();

    private final String zoneId;
    private Map<String, Dimension> dimensions;
    private final List<Ad> ads;

    public Zone(String zoneId) {
        this.zoneId = zoneId;
        this.dimensions = new HashMap<>();
        this.ads = new ArrayList<>();
    }

    public String getZoneId() {
        return zoneId;
    }

    public Map<String, Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Map<String, Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public List<Ad> getAds() {
        return ads;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "zoneId='" + zoneId + '\'' +
                ", dimensions=" + dimensions +
                ", ads=" + ads +
                '}';
    }
}
