package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/29/16.
 */

public class InitializeKeywordInterceptCommand {
    private final Session session;
    private final KeywordInterceptRequestBuilder builder;

    public InitializeKeywordInterceptCommand(final Session session,
                                             final KeywordInterceptRequestBuilder builder) {
        this.session = session;
        this.builder = builder;
    }

    public Session getSession() {
        return session;
    }

    public JSONObject getKeywordInterceptRequest() {
        return builder.buildInitRequest(session);
    }
}
