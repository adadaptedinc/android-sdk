package com.adadapted.android.sdk.core.addit.payload;


import com.adadapted.android.sdk.core.device.DeviceInfo;

public class PickupPayloadCommand {
    private final DeviceInfo deviceInfo;

    public PickupPayloadCommand(final DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
}
