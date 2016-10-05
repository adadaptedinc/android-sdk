package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 6/23/15.
 */
public interface KeywordInterceptRequestBuilder {
    JSONObject buildInitRequest(Session session);
}
