package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONObject;

public class InitializeSessionCommand {
    private final DeviceInfo deviceInfo;
    private final SessionRequestBuilder builder;

    public InitializeSessionCommand(final DeviceInfo deviceInfo,
                                    final SessionRequestBuilder builder) {
        this.deviceInfo = deviceInfo;
        this.builder = builder;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public JSONObject getSessionRequest() {
        return builder.buildSessionInitRequest(getDeviceInfo());
    }
}
