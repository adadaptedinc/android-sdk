package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONObject;

import java.util.Map;

public interface AdRefreshBuilder {
    Map<String, Zone> buildRefreshedAds(JSONObject adJson);
}
