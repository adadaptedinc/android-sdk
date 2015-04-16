package com.adadapted.android.sdk.core.zone;

import android.util.Log;

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

    private Map<String, Integer> adViews;
    private int zoneViews;
    private int adIndex;

    public Zone(String zoneId) {
        this.zoneId = zoneId;

        this.dimensions = new HashMap<>();
        this.ads = new ArrayList<>();

        this.adViews = new HashMap<>();
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

        for(Ad ad : ads) {
            adViews.put(ad.getAdId(), 0);

            zoneViews = 0;
            adIndex = 0;
        }
    }

    public int getAdCount() {
        return ads.size();
    }

    public Ad getNextAd(long adRefreshTime) {
        adIndex = zoneViews % ads.size();

        Ad ad = ads.get(adIndex);
        int count = adViews.get(ad.getAdId());

        if(ad.getMaxImpressions(adRefreshTime) < count) {
            ad.setImpressionViews(++count);
            adViews.put(ad.getAdId(), count);

            return ad;
        }
        else {
            Log.w(TAG, "Views for Ad " + ad.getAdId() + " in Zone " + zoneId + " have been exhausted for this period");
            return null;
        }
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
