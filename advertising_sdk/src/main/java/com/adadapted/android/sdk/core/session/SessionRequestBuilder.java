package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONObject;

public interface SessionRequestBuilder {
    JSONObject buildSessionInitRequest(DeviceInfo deviceInfo);
}
