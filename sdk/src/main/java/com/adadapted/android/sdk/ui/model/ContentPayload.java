package com.adadapted.android.sdk.ui.model;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.ContentAdAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class ContentPayload {
    private static final String LOGTAG = ContentPayload.class.getName();

    public static final int ADD_TO_LIST = 0;
    public static final int RECIPE_FAVORITE = 1;

    public static final String FIELD_ADD_TO_LIST_ITEMS = "add_to_list_items";

    private final ViewAdWrapper mAd;
    private final int mType;
    private final JSONObject mPayload;

    private ContentPayload(ViewAdWrapper ad, int type, JSONObject payload) {
        mAd = ad;
        mType = type;
        mPayload = payload;
    }

    public static ContentPayload createAddToListContent(ViewAdWrapper ad) {
        List items = ((ContentAdAction)ad.getAd().getAdAction()).getItems();
        JSONObject json = new JSONObject();

        try {
            json.put(FIELD_ADD_TO_LIST_ITEMS, new JSONArray(items));
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem parsing JSON");
        }

        return new ContentPayload(ad, ADD_TO_LIST, json);
    }

    public static ContentPayload createRecipeFavoriteContent(ViewAdWrapper ad) {
        return new ContentPayload(ad, RECIPE_FAVORITE, new JSONObject());
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
        return "ContentPayload";
    }
}
