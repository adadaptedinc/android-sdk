package com.adadapted.android.sdk.core.addit.payload;


import com.adadapted.android.sdk.core.common.Command;
import com.adadapted.android.sdk.core.device.DeviceInfo;

public class PickupPayloadCommand extends Command {
    private final DeviceInfo deviceInfo;

    public PickupPayloadCommand(final DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
}
