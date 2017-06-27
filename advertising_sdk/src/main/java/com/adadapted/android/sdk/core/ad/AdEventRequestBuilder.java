package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

public interface AdEventRequestBuilder {
    JSONObject build(Session session, Ad ad, String eventType, String eventName);
}
