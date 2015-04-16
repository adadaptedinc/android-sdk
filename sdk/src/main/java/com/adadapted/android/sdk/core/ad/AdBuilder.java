package com.adadapted.android.sdk.core.ad;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 3/27/15.
 */
public class AdBuilder {
    private static final String TAG = AdBuilder.class.getName();

    private static final int DEFAULT_REFRESH_TIME = 90;

    private static final String FIELD_AD_ID = "ad_id";
    private static final String FIELD_ZONE = "zone";
    private static final String FIELD_IMPRESSION_ID = "impression_id";
    private static final String FIELD_REFRESH_TIME = "refresh_time";
    private static final String FIELD_AD_TYPE = "ad_type";
    private static final String FIELD_ACTION_TYPE = "act_type";
    private static final String FIELD_ACTION_PATH = "act_path";
    private static final String FIELD_POPUP = "popup";
    private static final String FIELD_HIDE_AFTER_INTERACTION = "hide_after_interaction";
    private static final String FIELD_IMAGES = "images";

    private static final String AD_TYPE_HTML = "html";
    private static final String AD_TYPE_IMAGE = "image";
    private static final String AD_TYPE_JSON = "json";

    private static final String ACTION_TYPE_POPUP = "p";

    public List<Ad> buildAds(JSONArray jsonAds) {
        List<Ad> ads = new ArrayList<>();

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

    public Ad buildAd(JSONObject jsonAd) {
        Ad ad = new Ad();

        try {
            ad.setAdId(jsonAd.getString(FIELD_AD_ID));
            ad.setZoneId(jsonAd.getString(FIELD_ZONE));
            ad.setImpressionId(jsonAd.getString(FIELD_IMPRESSION_ID));

            try {
                ad.setRefreshTime(Integer.parseInt(jsonAd.getString(FIELD_REFRESH_TIME)));
            }
            catch(NumberFormatException ex) {
                Log.w(TAG, "Ad " + ad.getAdId() + " has an improperly set refresh_time.");
                ad.setRefreshTime(DEFAULT_REFRESH_TIME);
            }

            String adTypeCode = jsonAd.getString(FIELD_AD_TYPE);
            AdType adType = parseAdType(adTypeCode, jsonAd);
            ad.setAdType(adType);

            String actionTypeCode = jsonAd.getString(FIELD_ACTION_TYPE);
            AdAction adAction = parseAdAction(actionTypeCode, jsonAd);
            ad.setAdAction(adAction);

            ad.setHideAfterInteraction(jsonAd.getString(FIELD_HIDE_AFTER_INTERACTION));
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return ad;
    }

    private AdAction parseAdAction(String actionTypeCode, JSONObject jsonAd) {
        if(actionTypeCode.equalsIgnoreCase(ACTION_TYPE_POPUP)) {
            try {
                JSONObject popupObject = jsonAd.getJSONObject(FIELD_POPUP);
                String actionPath = jsonAd.getString(FIELD_ACTION_PATH);
                return parseAdPopup(popupObject, actionPath);
            }
            catch(JSONException ex) {
                Log.w(TAG, "Problem converting to JSON.", ex);
                return new NullAdAction();
            }
        }

        return new NullAdAction();
    }

    private PopupAdAction parseAdPopup(JSONObject popupJson, String actionPath) {
        PopupAdAction popup = new PopupAdAction();
        popup.setActionPath(actionPath);

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

    private AdType parseAdType(String adTypeCode, JSONObject jsonAd) {
        if(adTypeCode.equalsIgnoreCase(AD_TYPE_HTML)) {
            return parseHtmlAd(jsonAd);
        }
        else if(adTypeCode.equalsIgnoreCase(AD_TYPE_IMAGE)) {
            return parseImageAd(jsonAd);
        }
        else if(adTypeCode.equalsIgnoreCase(AD_TYPE_JSON)) {
            return parseJsonAd(jsonAd);
        }

        return new NullAdType();
    }

    private HtmlAdType parseHtmlAd(JSONObject jsonAd) {
        HtmlAdType adType = new HtmlAdType();

        return adType;
    }

    private ImageAdType parseImageAd(JSONObject jsonAd) {
        ImageAdType adType = new ImageAdType();
        try {
            Map<String, AdImage> images = parseImages(jsonAd.getJSONObject(FIELD_IMAGES));
            adType.setImages(images);
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return adType;
    }

    private JsonAdType parseJsonAd(JSONObject jsonAd) {
        JsonAdType adType = new JsonAdType();

        return adType;
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
