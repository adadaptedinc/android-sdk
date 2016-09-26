package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdBuilder;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdAction;
import com.adadapted.android.sdk.core.ad.model.AdComponent;
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
import com.adadapted.android.sdk.ext.factory.AnomalyTrackerFactory;

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
    private static final String LOGTAG = JsonAdBuilder.class.getName();

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

    public List<Ad> buildAds(final JSONArray jsonAds) {
        final List<Ad> ads = new ArrayList<>();

        try {
            int adCount = jsonAds.length();
            for(int i = 0; i < adCount; i++) {
                JSONObject jsonAd = jsonAds.getJSONObject(i);
                ads.add(buildAd(jsonAd));
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem converting to JSON.", ex);
            AnomalyTrackerFactory.registerAnomaly("",
                    jsonAds.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Failed to parse Session Ad payload for processing.");
        }

        return ads;
    }

    public Ad buildAd(final JSONObject jsonAd) throws JSONException {
        final Ad ad = new Ad();

        if(jsonAd.has(FIELD_AD_ID)) {
            ad.setAdId(jsonAd.getString(FIELD_AD_ID));
        }

        if(jsonAd.has(FIELD_ZONE)) {
            ad.setZoneId(jsonAd.getString(FIELD_ZONE));
        }

        if(jsonAd.has(FIELD_IMPRESSION_ID)) {
            ad.setImpressionId(jsonAd.getString(FIELD_IMPRESSION_ID));
        }

        try {
            ad.setRefreshTime(Integer.parseInt(jsonAd.getString(FIELD_REFRESH_TIME)));
        }
        catch(NumberFormatException ex) {
            Log.w(LOGTAG, "Ad " + ad.getAdId() + " has an improperly set refresh_time.");
            AnomalyTrackerFactory.registerAnomaly(
                    ad.getAdId(),
                    jsonAd.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Ad " + ad.getAdId() + " has an improperly set refresh_time.");
            ad.setRefreshTime(DEFAULT_REFRESH_TIME);
        }

        if(jsonAd.has(FIELD_AD_TYPE)) {
            final String adTypeCode = jsonAd.getString(FIELD_AD_TYPE);
            final AdType adType = parseAdType(adTypeCode, jsonAd);
            ad.setAdType(adType);
        }

        if(jsonAd.has(FIELD_ACTION_TYPE)) {
            final String actionTypeCode = jsonAd.getString(FIELD_ACTION_TYPE);
            final AdAction adAction = parseAdAction(actionTypeCode, jsonAd);
            ad.setAdAction(adAction);
        }

        if(jsonAd.has(FIELD_HIDE_AFTER_INTERACTION)) {
            ad.setHideAfterInteraction(jsonAd.getString(FIELD_HIDE_AFTER_INTERACTION).equals("1"));
        }

        return ad;
    }

    private AdAction parseAdAction(final String actionTypeCode,
                                   final JSONObject jsonAd) {
        if(actionTypeCode.equalsIgnoreCase(ACTION_TYPE_POPUP)) {
            try {
                final JSONObject popupObject = jsonAd.getJSONObject(FIELD_POPUP);
                final String actionPath = jsonAd.getString(FIELD_ACTION_PATH);

                return parseAdPopup(popupObject, actionPath);
            }
            catch(JSONException ex) {
                Log.w(LOGTAG, "Problem parsing to JSON.", ex);
                AnomalyTrackerFactory.registerAnomaly("",
                        jsonAd.toString(),
                        "SESSION_AD_PAYLOAD_PARSE_FAILED",
                        "Problem parsing to JSON.");
            }
        }
        else if(actionTypeCode.equalsIgnoreCase(ACTION_TYPE_DELEGATE)) {
            try {
                final String actionPath = jsonAd.getString(FIELD_ACTION_PATH);

                return parseAdDelegate(actionPath);
            }
            catch(JSONException ex) {
                Log.w(LOGTAG, "Problem parsing to JSON", ex);
                AnomalyTrackerFactory.registerAnomaly("",
                        jsonAd.toString(),
                        "SESSION_AD_PAYLOAD_PARSE_FAILED",
                        "Problem parsing to JSON.");
            }
        }
        else if(actionTypeCode.equalsIgnoreCase(ACTION_TYPE_CONTENT)) {
            try {
                final JSONObject payloadObject = jsonAd.getJSONObject(FIELD_PAYLOAD);
                final String actionPath = jsonAd.getString(FIELD_ACTION_PATH);

                return parseAdContent(payloadObject, actionPath);
            }
            catch(JSONException ex) {
                Log.w(LOGTAG, "Problem parsing to JSON", ex);
                AnomalyTrackerFactory.registerAnomaly("",
                        jsonAd.toString(),
                        "SESSION_AD_PAYLOAD_PARSE_FAILED",
                        "Problem parsing to JSON.");
            }
        }

        return new NullAdAction();
    }

    private ContentAdAction parseAdContent(final JSONObject payloadObject,
                                           final String actionPath) {
        final ContentAdAction content = new ContentAdAction();
        content.setActionPath(actionPath);

        final List<String> listItems = new ArrayList<>();

        try {
            final JSONArray jsonItems = payloadObject.getJSONArray("list-items");

            for(int i = 0; i < jsonItems.length(); i++) {
                listItems.add(jsonItems.getString(i));
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing to JSON", ex);
            AnomalyTrackerFactory.registerAnomaly("",
                    payloadObject.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Problem parsing to JSON.");
        }

        content.setItems(listItems);

        return content;
    }

    private DelegateAdAction parseAdDelegate(final String actionPath) {
        final DelegateAdAction delegate = new DelegateAdAction();
        delegate.setActionPath(actionPath);

        return delegate;
    }

    private PopupAdAction parseAdPopup(final JSONObject popupJson,
                                       final String actionPath) {
        final PopupAdAction popup = new PopupAdAction();
        popup.setActionPath(actionPath);

        try {
            if(popupJson.has("hide_banner")) {
                popup.setHideBanner(Boolean.parseBoolean(popupJson.getString("hide_banner")));
            }

            if(popupJson.has("title_text")) {
                popup.setTitle(popupJson.getString("title_text"));
            }

            if(popupJson.has("background_color")) {
                popup.setBackgroundColor(popupJson.getString("background_color"));
            }

            if(popupJson.has("text_color")) {
                popup.setTextColor(popupJson.getString("text_color"));
            }

            if(popupJson.has("alt_close_btn")) {
                popup.setAltCloseButton(popupJson.getString("alt_close_btn"));
            }

            if(popupJson.has("type")) {
                popup.setType(popupJson.getString("type"));
            }

            if(popupJson.has("hide_close_btn")) {
                popup.setHideCloseButton(Boolean.parseBoolean(popupJson.getString("hide_close_btn")));
            }

            if(popupJson.has("hide_browser_nav")) {
                popup.setHideBrowserNavigation(Boolean.parseBoolean(popupJson.getString("hide_browser_nav")));
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing to JSON", ex);
            AnomalyTrackerFactory.registerAnomaly("",
                    popupJson.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Problem parsing Popup JSON.");
        }

        return popup;
    }

    private AdType parseAdType(final String adTypeCode, final JSONObject jsonAd) {
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

    private HtmlAdType parseHtmlAd(final JSONObject jsonAd) {
        final HtmlAdType adType = new HtmlAdType();

        try {
            if(jsonAd.has(FIELD_AD_URL)) {
                adType.setAdUrl(jsonAd.getString(FIELD_AD_URL));
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing HTML JSON", ex);
            AnomalyTrackerFactory.registerAnomaly("",
                    jsonAd.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Problem parsing HTML JSON.");
        }

        return adType;
    }

    private ImageAdType parseImageAd(final JSONObject jsonAd) {
        final ImageAdType adType = new ImageAdType();
        try {
            final Map<String, AdImage> images = parseImages(jsonAd.getJSONObject(FIELD_IMAGES));
            adType.setImages(images);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing Image JSON", ex);
            AnomalyTrackerFactory.registerAnomaly("",
                    jsonAd.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Problem parsing Image JSON.");
        }

        return adType;
    }

    private JsonAdType parseJsonAd(JSONObject jsonAd) {
        final JsonAdType adType = new JsonAdType();

        try {
            final JSONObject jsonComponents = jsonAd.getJSONObject(FIELD_JSON);

            final AdComponent adComponents = parseJsonAdComponents(jsonComponents);
            adType.setComponents(adComponents);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing JSON", ex);
            AnomalyTrackerFactory.registerAnomaly("",
                    jsonAd.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Problem parsing JSON Display JSON.");
        }

        return adType;
    }

    private AdComponent parseJsonAdComponents(final JSONObject json) throws JSONException {
        final AdComponent adComponents = new AdComponent();

        if(json.has("ad_cta_1")) {
            adComponents.setCta1(json.getString("ad_cta_1"));
        }

        if(json.has("ad_cta_2")) {
            adComponents.setCta2(json.getString("ad_cta_2"));
        }

        if(json.has("ad_campaign_img")) {
            adComponents.setCampaignImage(json.getString("ad_campaign_img"));
        }

        if(json.has("ad_sponsor_logo")) {
            adComponents.setSponsorLogo(json.getString("ad_sponsor_logo"));
        }

        if(json.has("ad_sponsor_name")) {
            adComponents.setSponsorName(json.getString("ad_sponsor_name"));
        }

        if(json.has("ad_title")) {
            adComponents.setTitle(json.getString("ad_title"));
        }

        if(json.has("ad_tagline")) {
            adComponents.setTagLine(json.getString("ad_tagline"));
        }

        if(json.has("ad_text_long")) {
            adComponents.setLongText(json.getString("ad_text_long"));
        }

        if(json.has("ad_sponsor_text")) {
            adComponents.setSponsorText(json.getString("ad_sponsor_text"));
        }

        if(json.has("ad_app_icon_1")) {
            adComponents.setAppIcon1(json.getString("ad_app_icon_1"));
        }

        if(json.has("ad_app_icon_2")) {
            adComponents.setAppIcon2(json.getString("ad_app_icon_2"));
        }

        return adComponents;
    }

    private Map<String, AdImage> parseImages(final JSONObject jsonImages) {
        final Map<String, AdImage> images = new HashMap<>();

        try {
            for(final Iterator<String> imgRes = jsonImages.keys(); imgRes.hasNext();)
            {
                final String resKey = imgRes.next();
                final JSONArray orientation = jsonImages.getJSONArray(resKey);

                final AdImage image = new AdImage();
                for(int i = 0; i < orientation.length(); i++) {
                    final JSONObject orien = orientation.getJSONObject(i);
                    image.addOrientation(orien.getString(FIELD_IMAGE_ORIENTATION),
                            orien.getString(FIELD_IMAGE_URL));
                }

                images.put(resKey, image);
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing Image JSON", ex);
            AnomalyTrackerFactory.registerAnomaly("",
                    jsonImages.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Problem parsing Image JSON.");
        }

        return images;
    }
}
