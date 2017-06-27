package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

public interface AdRequestBuilder {
    JSONObject buildAdRequest(Session session);
}
