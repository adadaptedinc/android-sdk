package com.adadapted.android.sdk;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 3/27/15.
 */
class AdBuilder {
    private static final String TAG = AdBuilder.class.getName();

    Set<Ad> buildAds(JSONArray jsonAds) {
        Set<Ad> ads = new HashSet<>();

        try {
            int adCount = jsonAds.length();
            for(int i = 0; i < adCount; i++) {
                JSONObject jsonAd = jsonAds.getJSONObject(i);
                ads.add(buildAd(jsonAd));
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return ads;
    }

    Ad buildAd(JSONObject jsonAd) {
        Ad ad = new Ad();

        try {
            ad.setAdId(jsonAd.getString("ad_id"));
            ad.setZoneId(jsonAd.getString("zone"));
            ad.setImpressionId(jsonAd.getString("impression_id"));
            ad.setRefreshTime(Integer.parseInt(jsonAd.getString("refresh_time")));
            ad.setAdType(jsonAd.getString("ad_type"));
            ad.setActionType(jsonAd.getString("act_type"));
            ad.setActionPath(jsonAd.getString("act_path"));
            ad.setHideAfterInteraction(jsonAd.getString("hide_after_interaction"));

            if(ad.getAdType().equals("image")) {
                Map<String, AdImage> images = parseImages(jsonAd.getJSONObject("images"));
                ad.setImages(images);
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return ad;
    }

    private Map<String, AdImage> parseImages(JSONObject jsonImages) {
        Map<String, AdImage> images = new HashMap<>();

        try {
            for(Iterator<String> imgRes = jsonImages.keys(); imgRes.hasNext();)
            {
                String resKey = imgRes.next();
                JSONArray orientation = jsonImages.getJSONArray(resKey);

                AdImage image = new AdImage();
                for(int i = 0; i < orientation.length(); i++) {
                    JSONObject orien = orientation.getJSONObject(i);
                    image.addOrientation(orien.getString("orientation"), orien.getString("url"));
                }

                images.put(resKey, image);
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return images;
    }

    @Override
    public String toString() {
        return "AdBuilder{}";
    }
}
