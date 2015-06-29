package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdBuilder;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdAction;
import com.adadapted.android.sdk.core.ad.model.AdImage;
import com.adadapted.android.sdk.core.ad.model.AdType;
import com.adadapted.android.sdk.core.ad.model.ContentAdAction;
import com.adadapted.android.sdk.core.ad.model.DelegateAdAction;
import com.adadapted.android.sdk.core.ad.model.HtmlAdType;
import com.adadapted.android.sdk.core.ad.model.ImageAdType;
import com.adadapted.android.sdk.core.ad.model.JsonAdType;
import com.adadapted.android.sdk.core.ad.model.NullAdAction;
import com.adadapted.android.sdk.core.ad.model.NullAdType;
import com.adadapted.android.sdk.core.ad.model.PopupAdAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 6/29/15.
 */
public class JsonAdBuilder implements AdBuilder {
    private static final String TAG = JsonAdBuilder.class.getName();

    private static final int DEFAULT_REFRESH_TIME = 90;

    private static final String FIELD_AD_ID = "ad_id";
    private static final String FIELD_ZONE = "zone";
    private static final String FIELD_IMPRESSION_ID = "impression_id";
    private static final String FIELD_REFRESH_TIME = "refresh_time";
    private static final String FIELD_AD_TYPE = "ad_type";
    private static final String FIELD_ACTION_TYPE = "act_type";
    private static final String FIELD_ACTION_PATH = "act_path";
    private static final String FIELD_POPUP = "popup";
    private static final String FIELD_PAYLOAD = "payload";
    private static final String FIELD_HIDE_AFTER_INTERACTION = "hide_after_interaction";
    private static final String FIELD_IMAGES = "images";
    private static final String FIELD_JSON = "json";

    private static final String AD_TYPE_HTML = "html";
    private static final String AD_TYPE_IMAGE = "image";
    private static final String AD_TYPE_JSON = "json";

    private static final String ACTION_TYPE_POPUP = "p";
    private static final String ACTION_TYPE_DELEGATE = "d";
    private static final String ACTION_TYPE_CONTENT = "c";

    private static final String FIELD_AD_URL = "ad_url";

    private static final String FIELD_IMAGE_ORIENTATION = "orientation";
    private static final String FIELD_IMAGE_URL = "url";

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

            ad.setHideAfterInteraction(jsonAd.getString(FIELD_HIDE_AFTER_INTERACTION).equals("1"));
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
            }
        }
        else if(actionTypeCode.equalsIgnoreCase(ACTION_TYPE_DELEGATE)) {
            try {
                String actionPath = jsonAd.getString(FIELD_ACTION_PATH);
                return parseAdDelegate(actionPath);
            }
            catch(JSONException ex) {
                Log.w(TAG, "Problem converting to JSON.", ex);
            }
        }
        else if(actionTypeCode.equalsIgnoreCase(ACTION_TYPE_CONTENT)) {
            try {
                JSONObject payloadObject = jsonAd.getJSONObject(FIELD_PAYLOAD);
                String actionPath = jsonAd.getString(FIELD_ACTION_PATH);
                return parseAdContent(payloadObject, actionPath);
            }
            catch(JSONException ex) {
                Log.w(TAG, "Problem converting to JSON.", ex);
            }
        }

        return new NullAdAction();
    }

    private ContentAdAction parseAdContent(JSONObject payloadObject, String actionPath) {
        ContentAdAction content = new ContentAdAction();
        content.setActionPath(actionPath);

        List<String> listItems = new ArrayList<>();

        try {
            JSONArray jsonItems = payloadObject.getJSONArray("list-items");

            for(int i = 0; i < jsonItems.length(); i++) {
                listItems.add(jsonItems.getString(i));
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        content.setItems(listItems);

        return content;
    }

    private DelegateAdAction parseAdDelegate(String actionPath) {
        DelegateAdAction delegate = new DelegateAdAction();
        delegate.setActionPath(actionPath);

        return delegate;
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

        try {
            adType.setAdUrl(jsonAd.getString(FIELD_AD_URL));
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

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

        try {
            adType.json(jsonAd.getJSONObject(FIELD_JSON));
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

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
                    image.addOrientation(orien.getString(FIELD_IMAGE_ORIENTATION),
                            orien.getString(FIELD_IMAGE_URL));
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
