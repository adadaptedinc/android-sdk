package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/23/15.
 */
public interface SessionRequestBuilder {
    JSONObject buildSessionInitRequest(DeviceInfo deviceInfo);
}
