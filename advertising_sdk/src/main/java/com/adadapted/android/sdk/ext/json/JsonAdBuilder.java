package com.adadapted.android.sdk.ext.json;

import com.adadapted.android.sdk.core.ad.Ad;
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

    public List<Ad> buildAds(final JSONArray jsonAds) {
        final List<Ad> ads = new ArrayList<>();

        final int adCount = jsonAds.length();
        for(int i = 0; i < adCount; i++) {
            try {
                final JSONObject jsonAd = jsonAds.getJSONObject(i);
                if(jsonAd.getString(JsonFields.AD_TYPE).equals("html")) {
                    final Ad ad  = buildAd(jsonAd);
                    ads.add(ad);
                }
                else {
                    AppEventClient.trackError(
                        "SESSION_AD_PAYLOAD_PARSE_FAILED",
                        "Ad " + jsonAd.getString(JsonFields.ADID) + " has unsupported ad_type: " + jsonAd.getString(JsonFields.AD_TYPE)
                    );
                }
            }
            catch(JSONException ex) {
                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("bad_json", jsonAds.toString());
                errorParams.put("exception", ex.getMessage());

                AppEventClient.trackError(
                        "SESSION_AD_PAYLOAD_PARSE_FAILED",
                        "Problem parsing Image JSON.",
                        errorParams);
            }
        }

        return ads;
    }

    public Ad buildAd(final JSONObject jsonAd) throws JSONException {
        final Ad.Builder builder = new Ad.Builder();

        builder.setAdId(jsonAd.getString(JsonFields.ADID));
        builder.setZoneId(jsonAd.getString(JsonFields.ZONE));
        builder.setImpressionId(jsonAd.getString(JsonFields.IMPRESSIONID));

        try {
            builder.setRefreshTime(Integer.parseInt(jsonAd.getString(JsonFields.REFRESH_TIME)));
        }
        catch(NumberFormatException ex) {
            AppEventClient.trackError(
                "SESSION_AD_PAYLOAD_PARSE_FAILED",
                "Ad " + builder.getAdId() + " has an improperly set refresh_time."
            );

            builder.setRefreshTime(DEFAULT_REFRESH_TIME);
        }

        builder.setUrl(jsonAd.getString(JsonFields.AD_URL));

        builder.setActionType(jsonAd.getString(JsonFields.ACTION_TYPE));
        builder.setActionPath(jsonAd.getString(JsonFields.ACTION_PATH));

        if(builder.getActionType().equals(Ad.ActionTypes.CONTENT)) {
            builder.setPayload(parseAdContent(jsonAd));
        }

        builder.setTrackingHtml(jsonAd.getString(JsonFields.TRACKING_HTML));

        return builder.build();
    }

    private List<AddToListItem> parseAdContent(final JSONObject jsonAd) throws JSONException{
        final JSONObject payloadObject = jsonAd.getJSONObject(JsonFields.PAYLOAD);

        final List<AddToListItem> listItems = new ArrayList<>();
        if(payloadObject.has(JsonFields.CONTENT_LIST_ITEMS)) {
            final JSONArray jsonItems = payloadObject.getJSONArray(JsonFields.CONTENT_LIST_ITEMS);
            for (int i = 0; i < jsonItems.length(); i++) {
                final String name = jsonItems.getString(i);
                if(!name.isEmpty()) {
                    final AddToListItem item = new AddToListItem.Builder().setTitle(name).build();
                    listItems.add(item);
                }
            }
        } else if(payloadObject.has(JsonFields.CONTENT_DETAILED_LIST_ITEMS)) {
            final JSONArray jsonItems = payloadObject.getJSONArray(JsonFields.CONTENT_DETAILED_LIST_ITEMS);
            for (int i = 0; i < jsonItems.length(); i++) {
                final AddToListItem.Builder builder = new AddToListItem.Builder();

                if(payloadObject.has("product_title")) {
                    builder.setTitle(payloadObject.getString("product_title"));
                } else {
                    AppEventClient.trackError(
                        "SESSION_AD_PAYLOAD_PARSE_FAILED",
                        "Detailed List Items payload should always have a product title."
                    );

                    break;
                }

                if(payloadObject.has("product_brand")) {
                    builder.setBrand(payloadObject.getString("product_brand"));
                }

                if(payloadObject.has("product_category")) {
                    builder.setCategory(payloadObject.getString("product_category"));
                }

                if(payloadObject.has("product_barcode")) {
                    builder.setBarCode(payloadObject.getString("product_barcode"));
                }

                if(payloadObject.has("product_discount")) {
                    builder.setDiscount(payloadObject.getString("product_discount"));
                }

                if(payloadObject.has("product_image")) {
                    builder.setProductImage(payloadObject.getString("product_image"));
                }

                final AddToListItem item = builder.build();

                listItems.add(item);
            }
        } else if (payloadObject.has(JsonFields.CONTENT_RICH_LIST_ITEMS)) {
            final JSONArray jsonItems = payloadObject.getJSONArray(JsonFields.CONTENT_RICH_LIST_ITEMS);
            for (int i = 0; i < jsonItems.length(); i++) {
                final AddToListItem.Builder builder = new AddToListItem.Builder();

                if(payloadObject.has("product-title")) {
                    builder.setTitle(payloadObject.getString("product-title"));
                } else {
                    AppEventClient.trackError(
                            "SESSION_AD_PAYLOAD_PARSE_FAILED",
                            "Rich List Items payload should always have a product title."
                    );

                    break;
                }

                if(payloadObject.has("product-image")) {
                    builder.setTitle(payloadObject.getString("product-image"));
                }

                final AddToListItem item = builder.build();

                listItems.add(item);
            }
        }

        return listItems;
    }
}
