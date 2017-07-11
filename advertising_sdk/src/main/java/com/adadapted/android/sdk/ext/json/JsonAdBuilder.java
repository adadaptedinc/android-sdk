package com.adadapted.android.sdk.ext.json;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.ad.AdAnomalyClient;
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
                final Ad ad  = buildAd(jsonAd);

                ads.add(ad);
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
        builder.setImpressionId(jsonAd.getString(JsonFields.IMPRESSIONID));

        try {
            builder.setRefreshTime(Integer.parseInt(jsonAd.getString(JsonFields.REFRESH_TIME)));
        }
        catch(NumberFormatException ex) {
            AdAnomalyClient.trackAnomaly(
                builder.getAdId(),
                jsonAd.toString(),
                "SESSION_AD_PAYLOAD_PARSE_FAILED",
                "Ad " + builder.getAdId() + " has an improperly set refresh_time."
            );

            builder.setRefreshTime(DEFAULT_REFRESH_TIME);
        }

        final String adTypeCode = jsonAd.getString(JsonFields.AD_TYPE);
        if(adTypeCode.equals("html")) {
            builder.setUrl(jsonAd.getString(JsonFields.AD_URL));
        }
        else {
            AdAnomalyClient.trackAnomaly(
                builder.getAdId(),
                jsonAd.toString(),
                "SESSION_AD_PAYLOAD_PARSE_FAILED",
                "Ad " + builder.getAdId() + " has unsupported ad_type: " + adTypeCode
            );
        }

        builder.setActionType(jsonAd.getString(JsonFields.ACTION_TYPE));
        builder.setActionPath(jsonAd.getString(JsonFields.ACTION_PATH));
        builder.setPayload(jsonAd.getJSONObject(JsonFields.PAYLOAD));

        builder.setTrackingHtml(jsonAd.getString(JsonFields.TRACKING_HTML));

        return builder.build();
    }
}
