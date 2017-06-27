package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

public interface SessionBuilder {
    Session buildSession(JSONObject response);
}
