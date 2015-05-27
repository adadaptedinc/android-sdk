package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/23/15.
 */
public interface SessionRequestBuilder {
    JSONObject buildSessionRequest(DeviceInfo deviceInfo);
}
