package com.adadapted.android.sdk.core.zone.model;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.common.Dimension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class ManagedZone {
    private static final String TAG = ManagedZone.class.getName();

    private final Zone zone;

    private Map<String, Integer> adViews;

    public ManagedZone(final Zone zone) {
        this.zone = zone;
        this.adViews = new HashMap<>();
    }

    public Ad getNextAd() {
        return zone.getNextAd();

        //int count = adViews.get(ad.getAdId());
//
        //if(ad.getMaxImpressions(adRefreshTime) >= count) {
        //    ad.setImpressionViews(++count);
        //    adViews.put(ad.getAdId(), count);
//
        //    return ad;
        //}
        //else {
        //    Log.w(TAG, "Views for Ad " + ad.getAdId() + " in Zone " + zoneId + " have been exhausted for this period");
        //    return null;
        //}
    }

    public void setAds(List<Ad> ads) {
        adViews.clear();

        for(Ad ad : ads) {
            adViews.put(ad.getAdId(), 0);
        }

        zone.setAds(ads);
    }

    public boolean hasRemainingImpressions() {
        return false;
    }

    public int adCount() {
        return zone == null ? 0 : zone.getAds().size();
    }

    public boolean hasNoAds() {
        return adCount() == 0;
    }

    public Dimension getDimension(String orientation) {
        return zone.getDimensions().get(orientation);
    }
}
