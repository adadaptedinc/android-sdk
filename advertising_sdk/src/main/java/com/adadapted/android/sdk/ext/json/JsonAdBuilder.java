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
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;

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

    public List<Ad> buildAds(final JSONArray jsonAds) {
        final List<Ad> ads = new ArrayList<>();

        try {
            int adCount = jsonAds.length();
            for(int i = 0; i < adCount; i++) {
                final JSONObject jsonAd = jsonAds.getJSONObject(i);
                final Ad ad  = buildAd(jsonAd);

                ads.add(ad);
            }
        }
        catch(JSONException ex) {
            logJsonParseError(jsonAds.toString(), ex);
        }

        return ads;
    }

    public Ad buildAd(final JSONObject jsonAd) throws JSONException {
        final Ad ad = new Ad();

        if(jsonAd.has(JsonFields.ADID)) {
            ad.setAdId(jsonAd.getString(JsonFields.ADID));
        }

        if(jsonAd.has(JsonFields.ZONE)) {
            ad.setZoneId(jsonAd.getString(JsonFields.ZONE));
        }

        if(jsonAd.has(JsonFields.IMPRESSIONID)) {
            ad.setImpressionId(jsonAd.getString(JsonFields.IMPRESSIONID));
        }

        try {
            ad.setRefreshTime(Integer.parseInt(jsonAd.getString(JsonFields.REFRESH_TIME)));
        }
        catch(NumberFormatException ex) {
            AdAnomalyTrackingManager.registerAnomaly(
                    ad.getAdId(),
                    jsonAd.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Ad " + ad.getAdId() + " has an improperly set refresh_time.");
            ad.setRefreshTime(DEFAULT_REFRESH_TIME);
        }

        if(jsonAd.has(JsonFields.AD_TYPE)) {
            final String adTypeCode = jsonAd.getString(JsonFields.AD_TYPE);
            final AdType adType = parseAdType(adTypeCode, jsonAd);
            ad.setAdType(adType);
        }

        if(jsonAd.has(JsonFields.ACTION_TYPE)) {
            final String actionTypeCode = jsonAd.getString(JsonFields.ACTION_TYPE);
            final AdAction adAction = parseAdAction(actionTypeCode, jsonAd);
            ad.setAdAction(adAction);
        }

        if(jsonAd.has(JsonFields.HIDE_AFTER_INTERACTION)) {
            ad.setHideAfterInteraction(jsonAd.getString(JsonFields.HIDE_AFTER_INTERACTION).equals("1"));
        }

        return ad;
    }

    private AdAction parseAdAction(final String actionTypeCode,
                                   final JSONObject jsonAd) {
        if(actionTypeCode.equalsIgnoreCase(AdAction.POPUP)) {
            try {
                final JSONObject popupObject = jsonAd.getJSONObject(JsonFields.POPUP);
                final String actionPath = jsonAd.getString(JsonFields.ACTION_PATH);

                return parseAdPopup(popupObject, actionPath);
            }
            catch(JSONException ex) {
                logJsonParseError(jsonAd.toString(), ex);
            }
        }
        else if(actionTypeCode.equalsIgnoreCase(AdAction.DELEGATE)) {
            try {
                final String actionPath = jsonAd.getString(JsonFields.ACTION_PATH);

                return parseAdDelegate(actionPath);
            }
            catch(JSONException ex) {
                logJsonParseError(jsonAd.toString(), ex);
            }
        }
        else if(actionTypeCode.equalsIgnoreCase(AdAction.CONTENT)) {
            try {
                final JSONObject payloadObject = jsonAd.getJSONObject(JsonFields.PAYLOAD);
                final String actionPath = jsonAd.getString(JsonFields.ACTION_PATH);

                return parseAdContent(payloadObject, actionPath);
            }
            catch(JSONException ex) {
                logJsonParseError(jsonAd.toString(), ex);
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
            final JSONArray jsonItems = payloadObject.getJSONArray(JsonFields.CONTENT_LIST_ITEMS);

            for(int i = 0; i < jsonItems.length(); i++) {
                listItems.add(jsonItems.getString(i));
            }
        }
        catch(JSONException ex) {
            logJsonParseError(payloadObject.toString(), ex);
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
            if(popupJson.has(JsonFields.POPUP_HIDE_BANNER)) {
                popup.setHideBanner(Boolean.parseBoolean(popupJson.getString(JsonFields.POPUP_HIDE_BANNER)));
            }

            if(popupJson.has(JsonFields.POPUP_TITLE_TEXT)) {
                popup.setTitle(popupJson.getString(JsonFields.POPUP_TITLE_TEXT));
            }

            if(popupJson.has(JsonFields.POPUP_BACKGROUND_COLOR)) {
                popup.setBackgroundColor(popupJson.getString(JsonFields.POPUP_BACKGROUND_COLOR));
            }

            if(popupJson.has(JsonFields.POPUP_TEXT_COLOR)) {
                popup.setTextColor(popupJson.getString(JsonFields.POPUP_TEXT_COLOR));
            }

            if(popupJson.has(JsonFields.POPUP_ALT_CLOSE_BTN)) {
                popup.setAltCloseButton(popupJson.getString(JsonFields.POPUP_ALT_CLOSE_BTN));
            }

            if(popupJson.has(JsonFields.POPUP_TYPE)) {
                popup.setType(popupJson.getString(JsonFields.POPUP_TYPE));
            }

            if(popupJson.has(JsonFields.POPUP_HIDE_CLOSE_BTN)) {
                popup.setHideCloseButton(Boolean.parseBoolean(popupJson.getString(JsonFields.POPUP_HIDE_CLOSE_BTN)));
            }

            if(popupJson.has(JsonFields.POPUP_HIDE_BROWSER_NAV)) {
                popup.setHideBrowserNavigation(Boolean.parseBoolean(popupJson.getString(JsonFields.POPUP_HIDE_BROWSER_NAV)));
            }
        }
        catch(JSONException ex) {
            logJsonParseError(popupJson.toString(), ex);
        }

        return popup;
    }

    private AdType parseAdType(final String adTypeCode, final JSONObject jsonAd) {
        if(adTypeCode.equalsIgnoreCase(AdType.HTML)) {
            return parseHtmlAd(jsonAd);
        }
        else if(adTypeCode.equalsIgnoreCase(AdType.IMAGE)) {
            return parseImageAd(jsonAd);
        }
        else if(adTypeCode.equalsIgnoreCase(AdType.JSON)) {
            return parseJsonAd(jsonAd);
        }

        Log.w(LOGTAG, "Unsupported Ad Type: " + adTypeCode);

        return new NullAdType();
    }

    private HtmlAdType parseHtmlAd(final JSONObject jsonAd) {
        final HtmlAdType adType = new HtmlAdType();

        try {
            if(jsonAd.has(JsonFields.AD_URL)) {
                adType.setAdUrl(jsonAd.getString(JsonFields.AD_URL));
            }
        }
        catch(JSONException ex) {
            logJsonParseError(jsonAd.toString(), ex);
        }

        return adType;
    }

    private ImageAdType parseImageAd(final JSONObject jsonAd) {
        final ImageAdType adType = new ImageAdType();
        try {
            final Map<String, AdImage> images = parseImages(jsonAd.getJSONObject(JsonFields.IMAGES));
            adType.setImages(images);
        }
        catch(JSONException ex) {
            logJsonParseError(jsonAd.toString(), ex);
        }

        return adType;
    }

    private JsonAdType parseJsonAd(JSONObject jsonAd) {
        final JsonAdType adType = new JsonAdType();

        try {
            final JSONObject jsonComponents = jsonAd.getJSONObject(JsonFields.JSON);

            final AdComponent adComponents = parseJsonAdComponents(jsonComponents);
            adType.setComponents(adComponents);
        }
        catch(JSONException ex) {
            logJsonParseError(jsonAd.toString(), ex);
        }

        return adType;
    }

    private AdComponent parseJsonAdComponents(final JSONObject json) throws JSONException {
        final AdComponent adComponents = new AdComponent();

        if(json.has(JsonFields.JSON_AD_CTA_1)) {
            adComponents.setCta1(json.getString(JsonFields.JSON_AD_CTA_1));
        }

        if(json.has(JsonFields.JSON_AD_CTA_2)) {
            adComponents.setCta2(json.getString(JsonFields.JSON_AD_CTA_2));
        }

        if(json.has(JsonFields.JSON_AD_CAMPAIGN_IMG)) {
            adComponents.setCampaignImage(json.getString(JsonFields.JSON_AD_CAMPAIGN_IMG));
        }

        if(json.has(JsonFields.JSON_AD_SPONSOR_LOGO)) {
            adComponents.setSponsorLogo(json.getString(JsonFields.JSON_AD_SPONSOR_LOGO));
        }

        if(json.has(JsonFields.JSON_AD_SPONSOR_NAME)) {
            adComponents.setSponsorName(json.getString(JsonFields.JSON_AD_SPONSOR_NAME));
        }

        if(json.has(JsonFields.JSON_AD_TITLE)) {
            adComponents.setTitle(json.getString(JsonFields.JSON_AD_TITLE));
        }

        if(json.has(JsonFields.JSON_AD_TAGLINE)) {
            adComponents.setTagLine(json.getString(JsonFields.JSON_AD_TAGLINE));
        }

        if(json.has(JsonFields.JSON_AD_TEXT_LONG)) {
            adComponents.setLongText(json.getString(JsonFields.JSON_AD_TEXT_LONG));
        }

        if(json.has(JsonFields.JSON_AD_SPONSOR_TEXT)) {
            adComponents.setSponsorText(json.getString(JsonFields.JSON_AD_SPONSOR_TEXT));
        }

        if(json.has(JsonFields.JSON_AD_APP_ICON_1)) {
            adComponents.setAppIcon1(json.getString(JsonFields.JSON_AD_APP_ICON_1));
        }

        if(json.has(JsonFields.JSON_AD_APP_ICON_2)) {
            adComponents.setAppIcon2(json.getString(JsonFields.JSON_AD_APP_ICON_2));
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
                    image.addOrientation(orien.getString(JsonFields.IMAGE_ORIENTATION),
                            orien.getString(JsonFields.IMAGE_URL));
                }

                images.put(resKey, image);
            }
        }
        catch(JSONException ex) {
            logJsonParseError(jsonImages.toString(), ex);
        }

        return images;
    }

    private void logJsonParseError(final String errorJson, final Throwable ex) {
        Log.w(LOGTAG, "Problem parsing Image JSON", ex);

        final Map<String, String> errorParams = new HashMap<>();
        errorParams.put("bad_json", errorJson);
        errorParams.put("exception", ex.getMessage());

        AppErrorTrackingManager.registerEvent(
                "SESSION_AD_PAYLOAD_PARSE_FAILED",
                "Problem parsing Image JSON.",
                errorParams);
    }
}
