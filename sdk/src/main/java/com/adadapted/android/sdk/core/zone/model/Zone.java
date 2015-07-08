package com.adadapted.android.sdk.core.zone.model;

import com.adadapted.android.sdk.core.ad.model.Ad;
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
    private final List<Ad> ads;

    private Map<String, Dimension> dimensions;
    private int zoneViews = 0;
    private int adIndex = 0;

    public Zone(String zoneId) {
        this.zoneId = (zoneId == null) ? "" : zoneId;

        this.dimensions = new HashMap<>();
        this.ads = new ArrayList<>();
    }

    public static Zone createEmptyZone(String zoneId) {
        return new Zone(zoneId);
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

    public void setAds(List<Ad> ads) {
        this.ads.clear();
        this.ads.addAll(ads);

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
        Ad ad = ads.get(adIndex);

        if(ad.isHidden()) {
            return getNextAd();
        }

        return ad;
    }

    public boolean hasVisibleAds() {
        boolean result = true;

        for(Ad ad : getAds()) {
            result |= ad.isNotHidden();
        }

        return result;
    }

    public Ad getCurrentAd() {
        return ads.get(adIndex);
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
