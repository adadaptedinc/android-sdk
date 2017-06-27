package com.adadapted.android.sdk.core.zone.model;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.common.Dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zone {
    private static final String TAG = Zone.class.getName();

    private final String zoneId;
    private List<Ad> ads;
    private Map<String, Dimension> dimensions;

    private int zoneViews;
    private int adIndex;

    public Zone(final String zoneId) {
        this.zoneId = (zoneId == null) ? "" : zoneId;
        this.ads = new ArrayList<>();
        this.dimensions = new HashMap<>();

        zoneViews = 0;
        adIndex = 0;
    }

    public static Zone createEmptyZone(final String zoneId) {
        return new Zone(zoneId);
    }

    public String getZoneId() {
        return zoneId;
    }

    public Map<String, Dimension> getDimensions() {
        return dimensions;
    }

    public Dimension getDimension(final String orientation) {
        return getDimensions().get(orientation);
    }

    public void setDimensions(final Map<String, Dimension> dimensions) {
        this.dimensions = new HashMap<>(dimensions);
    }

    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(final List<Ad> ads) {
        this.ads = new ArrayList<>(ads);

        zoneViews = 0;
        adIndex = 0;
    }

    public int getAdCount() {
        return ads.size();
    }

    public Ad getNextAd() {
        if(!hasVisibleAds()) {
            return null;
        }

        adIndex = ++zoneViews % getAdCount();
        final Ad ad = ads.get(adIndex);

        if(ad.isHidden()) {
            return getNextAd();
        }

        return ad;
    }

    public boolean hasVisibleAds() {
        boolean result = false;

        for(final Ad ad : getAds()) {
            result |= ad.isNotHidden();
        }

        return result;
    }
}
