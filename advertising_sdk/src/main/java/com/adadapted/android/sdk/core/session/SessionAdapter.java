package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.common.PositiveListener;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/26/15.
 */
public interface SessionAdapter {
    void sendInit(JSONObject request, Callback listener);

    interface Callback extends PositiveListener<Session> {}
}
