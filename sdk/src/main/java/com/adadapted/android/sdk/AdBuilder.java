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

    private static final int DEFAULT_REFRESH_TIME = 90;

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

            try {
                ad.setRefreshTime(Integer.parseInt(jsonAd.getString("refresh_time")));
            }
            catch(NumberFormatException ex) {
                Log.w(TAG, "Ad " + ad.getAdId() + " has an improperly set refresh_time.");
                ad.setRefreshTime(DEFAULT_REFRESH_TIME);
            }

            ad.setAdType(jsonAd.getString("ad_type"));
            ad.setActionType(jsonAd.getString("act_type"));
            ad.setActionPath(jsonAd.getString("act_path"));
            ad.setHideAfterInteraction(jsonAd.getString("hide_after_interaction"));

            if(ad.getAdType().equals("image")) {
                Map<String, AdImage> images = parseImages(jsonAd.getJSONObject("images"));
                ad.setImages(images);
            }
            else if(ad.getAdType().equals("html")) {

            }
            else if(ad.getAdType().equals("json")) {

            }

            if(ad.getActionType().equals("p")) {
                AdPopup popup = parseAdPopup(jsonAd.getJSONObject("popup"));
                ad.setAdAction(popup);
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return ad;
    }

    private AdPopup parseAdPopup(JSONObject popupJson) {
        AdPopup popup = new AdPopup();

        try {
            popup.setHideBanner(Boolean.parseBoolean(popupJson.getString("hide_banner")));
            popup.setTitle(popupJson.getString("title_text"));
            popup.setBackgroundColor(popupJson.getString("background_color"));
            popup.setTextColor(popupJson.getString("text_color"));
            popup.setAltCloseButton(popupJson.getString("alt_close_btn"));
            popup.setType(popupJson.getString("type"));
            popup.setHideCloseButton(Boolean.parseBoolean(popupJson.getString("hide_close_btn")));
            popup.setHideBrowserNavigation(Boolean.parseBoolean(popupJson.getString("hide_browser_nav")));
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return popup;
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
