package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/23/15.
 */
public interface SessionBuilder {
    Session buildSession(DeviceInfo deviceInfo, JSONObject response);
}
