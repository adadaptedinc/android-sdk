package com.adadapted.android.sdk.core.session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/26/15.
 */
public interface SessionAdapter {
    void sendInit(JSONObject request, SessionAdapterListener listener);
}
