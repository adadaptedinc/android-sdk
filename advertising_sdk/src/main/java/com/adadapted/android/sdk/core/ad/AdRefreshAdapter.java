package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.common.PositiveListener;
import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 4/1/15.
 */
public interface AdRefreshAdapter {
    void getAds(JSONObject json, Callback callback);

    interface Callback extends PositiveListener<Map<String, Zone>> {}
}
