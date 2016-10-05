package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/29/16.
 */

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
