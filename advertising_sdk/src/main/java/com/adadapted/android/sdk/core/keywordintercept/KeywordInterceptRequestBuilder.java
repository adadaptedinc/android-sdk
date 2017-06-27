package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

public interface KeywordInterceptRequestBuilder {
    JSONObject buildInitRequest(Session session);
}
