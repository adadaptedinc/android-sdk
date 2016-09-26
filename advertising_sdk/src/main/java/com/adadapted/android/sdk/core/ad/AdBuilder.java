package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.ad.model.Ad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by chrisweeden on 3/27/15.
 */
public interface AdBuilder {
    List<Ad> buildAds(JSONArray jsonAds);
    Ad buildAd(JSONObject jsonAd) throws JSONException;
}
