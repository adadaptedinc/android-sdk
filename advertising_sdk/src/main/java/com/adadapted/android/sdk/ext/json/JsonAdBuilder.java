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
import com.adadapted.android.sdk.core.ad.model.LinkAdAction;
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

public class JsonAdBuilder implements AdBuilder {
    private static final String LOGTAG = JsonAdBuilder.class.getName();

    private static final int DEFAULT_REFRESH_TIME = 90;

    public List<Ad> buildAds(final JSONArray jsonAds) {
        final List<Ad> ads = new ArrayList<>();

        final int adCount = jsonAds.length();
        for(int i = 0; i < adCount; i++) {
            try {
                final JSONObject jsonAd = jsonAds.getJSONObject(i);
                final Ad ad  = buildAd(jsonAd);

                ads.add(ad);
            }
            catch(JSONException ex) {
                logJsonParseError(jsonAds.toString(), ex);
            }
        }

        return ads;
    }

    public Ad buildAd(final JSONObject jsonAd) throws JSONException {
        final Ad.Builder builder = new Ad.Builder();

        builder.setAdId(jsonAd.getString(JsonFields.ADID));
        builder.setZoneId(jsonAd.getString(JsonFields.ZONE));
        builder.setBaseImpressionId(jsonAd.getString(JsonFields.IMPRESSIONID));

        try {
            builder.setRefreshTime(Integer.parseInt(jsonAd.getString(JsonFields.REFRESH_TIME)));
        }
        catch(NumberFormatException ex) {
            AdAnomalyTrackingManager.registerAnomaly(
                builder.getAdId(),
                jsonAd.toString(),
                "SESSION_AD_PAYLOAD_PARSE_FAILED",
                "Ad " + builder.getAdId() + " has an improperly set refresh_time."
            );

            builder.setRefreshTime(DEFAULT_REFRESH_TIME);
        }

        final String adTypeCode = jsonAd.getString(JsonFields.AD_TYPE);
        final AdType adType = parseAdType(adTypeCode, jsonAd);
        builder.setAdType(adType);

        final String actionTypeCode = jsonAd.getString(JsonFields.ACTION_TYPE);
        final AdAction adAction = parseAdAction(actionTypeCode, jsonAd);
        builder.setAdAction(adAction);

        builder.setHideAfterInteraction(jsonAd.getString(JsonFields.HIDE_AFTER_INTERACTION).equals("1"));
        builder.setTrackingHtml(jsonAd.getString(JsonFields.TRACKING_HTML));

        return builder.build();
    }

    private AdAction parseAdAction(final String actionTypeCode, final JSONObject jsonAd) throws JSONException {
        if(actionTypeCode.equalsIgnoreCase(AdAction.POPUP)) {
            final JSONObject popupObject = jsonAd.getJSONObject(JsonFields.POPUP);
            final String actionPath = jsonAd.getString(JsonFields.ACTION_PATH);

            return parseAdPopup(popupObject, actionPath);
        }
        else if(actionTypeCode.equalsIgnoreCase(AdAction.DELEGATE)) {
            final String actionPath = jsonAd.getString(JsonFields.ACTION_PATH);

            return parseAdDelegate(actionPath);
        }
        else if(actionTypeCode.equalsIgnoreCase(AdAction.CONTENT)) {
            final JSONObject payloadObject = jsonAd.getJSONObject(JsonFields.PAYLOAD);
            final String actionPath = jsonAd.getString(JsonFields.ACTION_PATH);

            return parseAdContent(payloadObject, actionPath);
        }
        else if(actionTypeCode.equalsIgnoreCase(AdAction.LINK)) {
            final String actionPath = jsonAd.getString(JsonFields.ACTION_PATH);

            return parseAdLink(actionPath);
        }

        return new NullAdAction();
    }

    private ContentAdAction parseAdContent(final JSONObject payloadObject, final String actionPath) throws JSONException{
        final JSONArray jsonItems = payloadObject.getJSONArray(JsonFields.CONTENT_LIST_ITEMS);

        final List<String> listItems = new ArrayList<>();
        for(int i = 0; i < jsonItems.length(); i++) {
            listItems.add(jsonItems.getString(i));
        }

        final ContentAdAction content = new ContentAdAction();
        content.setActionPath(actionPath);
        content.setItems(listItems);

        return content;
    }

    private DelegateAdAction parseAdDelegate(final String actionPath) {
        final DelegateAdAction delegate = new DelegateAdAction();
        delegate.setActionPath(actionPath);

        return delegate;
    }

    private LinkAdAction parseAdLink(final String actionPath) {
        final LinkAdAction link = new LinkAdAction();
        link.setActionPath(actionPath);

        return link;
    }

    private PopupAdAction parseAdPopup(final JSONObject popupJson, final String actionPath) throws JSONException {
        final PopupAdAction popup = new PopupAdAction();

        popup.setActionPath(actionPath);
        popup.setHideBanner(Boolean.parseBoolean(popupJson.getString(JsonFields.POPUP_HIDE_BANNER)));
        popup.setTitle(popupJson.getString(JsonFields.POPUP_TITLE_TEXT));
        popup.setBackgroundColor(popupJson.getString(JsonFields.POPUP_BACKGROUND_COLOR));
        popup.setTextColor(popupJson.getString(JsonFields.POPUP_TEXT_COLOR));
        popup.setAltCloseButton(popupJson.getString(JsonFields.POPUP_ALT_CLOSE_BTN));
        popup.setType(popupJson.getString(JsonFields.POPUP_TYPE));
        popup.setHideCloseButton(Boolean.parseBoolean(popupJson.getString(JsonFields.POPUP_HIDE_CLOSE_BTN)));
        popup.setHideBrowserNavigation(Boolean.parseBoolean(popupJson.getString(JsonFields.POPUP_HIDE_BROWSER_NAV)));

        return popup;
    }

    private AdType parseAdType(final String adTypeCode, final JSONObject jsonAd) throws JSONException {
        if(adTypeCode.equalsIgnoreCase(AdType.HTML)) {
            return parseHtmlAd(jsonAd);
        }
        else if(adTypeCode.equalsIgnoreCase(AdType.IMAGE)) {
            return parseImageAd(jsonAd);
        }

        Log.w(LOGTAG, "Unsupported Ad Type: " + adTypeCode);

        return new NullAdType();
    }

    private HtmlAdType parseHtmlAd(final JSONObject jsonAd) throws JSONException {
        final HtmlAdType adType = new HtmlAdType();
        adType.setAdUrl(jsonAd.getString(JsonFields.AD_URL));

        return adType;
    }

    private ImageAdType parseImageAd(final JSONObject jsonAd) throws JSONException {
        final ImageAdType adType = new ImageAdType();
        final Map<String, AdImage> images = parseImages(jsonAd.getJSONObject(JsonFields.IMAGES));
        adType.setImages(images);

        return adType;
    }

    private Map<String, AdImage> parseImages(final JSONObject jsonImages) throws JSONException {
        final Map<String, AdImage> images = new HashMap<>();

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
