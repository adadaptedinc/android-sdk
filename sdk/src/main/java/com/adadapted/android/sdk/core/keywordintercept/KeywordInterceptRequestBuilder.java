package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.keywordintercept.model.KeywordInterceptEvent;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by chrisweeden on 6/23/15.
 */
public interface KeywordInterceptRequestBuilder {
    JSONObject buildInitRequest(Session session);
    JSONArray buildTrackRequest(Set<KeywordInterceptEvent> keywordInterceptEvents);
}
