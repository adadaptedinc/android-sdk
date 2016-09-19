package com.adadapted.android.sdk.ui.model;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.ContentAdAction;
import com.adadapted.android.sdk.core.content.ContentPayload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class AdContentPayload implements ContentPayload {
    private static final String LOGTAG = AdContentPayload.class.getName();

    public static final int ADD_TO_LIST = 0;
    public static final int RECIPE_FAVORITE = 1;

    public static final String FIELD_ADD_TO_LIST_ITEMS = "add_to_list_items";

    private final ViewAdWrapper mAd;
    private final int mType;
    private final JSONObject mPayload;

    private AdContentPayload(final ViewAdWrapper ad,
                             final int type,
                             final JSONObject payload) {
        mAd = ad;
        mType = type;
        mPayload = payload;
    }

    public static AdContentPayload createAddToListContent(final ViewAdWrapper ad) {
        List items = ((ContentAdAction)ad.getAd().getAdAction()).getItems();
        JSONObject json = new JSONObject();

        try {
            json.put(FIELD_ADD_TO_LIST_ITEMS, new JSONArray(items));
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing JSON");
        }

        return new AdContentPayload(ad, ADD_TO_LIST, json);
    }

    public static AdContentPayload createRecipeFavoriteContent(final ViewAdWrapper ad) {
        return new AdContentPayload(ad, RECIPE_FAVORITE, new JSONObject());
    }

    public void acknowledge() {
        Log.d(LOGTAG, "Content Payload acknowledged.");
        mAd.trackPayloadDelivered();
    }

    public String getZoneId() {
        return mAd.getAd().getZoneId();
    }

    public int getType() {
        return mType;
    }

    public JSONObject getPayload() {
        return mPayload;
    }

    @Override
    public String toString() {
        return "AdContentPayload";
    }
}
