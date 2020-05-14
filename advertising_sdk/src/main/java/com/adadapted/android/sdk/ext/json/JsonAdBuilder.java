package com.adadapted.android.sdk.ext.json;

import com.adadapted.android.sdk.core.ad.AdActionType;
import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdDisplayType;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.event.AppEventClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonAdBuilder {
    @SuppressWarnings("unused")
    private static final String LOGTAG = JsonAdBuilder.class.getName();

    private static final int DEFAULT_REFRESH_TIME = 90;

    private static final String AD_ID = "ad_id";
    private static final String IMPRESSION_ID = "impression_id";
    private static final String REFRESH_TIME  = "refresh_time";
    private static final String HIDE_AFTER_INTERACTION = "hide_after_interaction";
    private static final String TYPE = "type";
    private static final String CREATIVE_URL = "creative_url";
    private static final String ACTION_TYPE = "action_type";
    private static final String ACTION_PATH = "action_path";
    private static final String PAYLOAD = "payload";
    private static final String POPUP = "popup";
    private static final String TRACKING_HTML = "tracking_html";
    private static final String SETTINGS = "settings";

    private static final String CONTENT_DETAILED_LIST_ITEMS = "detailed_list_items";
    private static final String PRODUCT_TITLE = "product_title";
    private static final String PRODUCT_BRAND = "product_brand";
    private static final String PRODUCT_CATEGORY = "product_category";
    private static final String PRODUCT_BARCODE = "product_barcode";
    private static final String PRODUCT_SKU = "product_sku";
    private static final String PRODUCT_DISCOUNT = "product_discount";
    private static final String PRODUCT_IMAGE = "product_image";


    public List<Ad> buildAds(final String zoneId, final JSONArray jsonAds) {
        final List<Ad> ads = new ArrayList<>();

        final int adCount = jsonAds.length();
        for(int i = 0; i < adCount; i++) {
            try {
                final JSONObject ad = jsonAds.getJSONObject(i);
                if(ad.getString(TYPE).equals(AdDisplayType.HTML)) {
                    ads.add(buildAd(zoneId, ad));
                }
                else {
                    AppEventClient.trackError(
                        "AD_PAYLOAD_PARSE_FAILED",
                        "Ad " + ad.getString(AD_ID) + " has unsupported ad_type: " + ad.getString(TYPE)
                    );
                }
            }
            catch(JSONException ex) {
                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("bad_json", jsonAds.toString());
                errorParams.put("exception", ex.getMessage());

                AppEventClient.trackError(
                    "AD_PAYLOAD_PARSE_FAILED",
                    "Problem parsing Ad JSON.",
                    errorParams
                );
            }
        }

        return ads;
    }

    public Ad buildAd(final String zoneId, final JSONObject ad) throws JSONException {
        int parsedRefreshTime = DEFAULT_REFRESH_TIME;
        try {
            parsedRefreshTime = Integer.parseInt(ad.getString(REFRESH_TIME));
        }
        catch(NumberFormatException ex) {
            AppEventClient.trackError(
                    "AD_PAYLOAD_PARSE_FAILED",
                    "Ad " + ad.get(AD_ID) + " has an improperly set refresh_time."
            );
        }

        List<AddToListItem> payload = new ArrayList<>();
        if (AdActionType.INSTANCE.handlesContent(ad.getString(ACTION_TYPE))) {
            payload = parseAdContent(ad);
        }

        final Ad newAd = new Ad(
                ad.getString(AD_ID),
                zoneId,
                ad.getString(IMPRESSION_ID),
                ad.getString(CREATIVE_URL),
                ad.getString(ACTION_TYPE),
                ad.getString(ACTION_PATH),
                payload,
                parsedRefreshTime,
                ad.getString(TRACKING_HTML));

        return newAd;
    }

    private List<AddToListItem> parseAdContent(final JSONObject ad) throws JSONException{
        final JSONObject payloadObject = ad.getJSONObject(PAYLOAD);
        return parseDetailedListItems(payloadObject.getJSONArray(CONTENT_DETAILED_LIST_ITEMS));
    }

    private List<AddToListItem> parseDetailedListItems(final JSONArray items) throws JSONException {
        final List<AddToListItem> listItems = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            final JSONObject item = items.getJSONObject(i);
            final AddToListItem.Builder builder = new AddToListItem.Builder();

            if(item.has(PRODUCT_TITLE)) {
                builder.setTitle(item.getString(PRODUCT_TITLE));
            } else {
                AppEventClient.trackError(
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Detailed List Items payload should always have a product title."
                );

                break;
            }

            if(item.has(PRODUCT_BRAND)) {
                builder.setBrand(item.getString(PRODUCT_BRAND));
            }

            if(item.has(PRODUCT_CATEGORY)) {
                builder.setCategory(item.getString(PRODUCT_CATEGORY));
            }

            if(item.has(PRODUCT_BARCODE)) {
                builder.setProductUpc(item.getString(PRODUCT_BARCODE));
            }

            if(item.has(PRODUCT_SKU)) {
                builder.setRetailerSku(item.getString(PRODUCT_SKU));
            }

            if(item.has(PRODUCT_DISCOUNT)) {
                builder.setDiscount(item.getString(PRODUCT_DISCOUNT));
            }

            if(item.has(PRODUCT_IMAGE)) {
                builder.setProductImage(item.getString(PRODUCT_IMAGE));
            }

            listItems.add(builder.build());
        }

        return listItems;
    }
}
