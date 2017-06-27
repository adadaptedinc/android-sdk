package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.ad.model.Ad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface AdBuilder {
    List<Ad> buildAds(JSONArray jsonAds);
    Ad buildAd(JSONObject jsonAd) throws JSONException;
}
