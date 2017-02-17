package com.adadapted.sdk.addit.core.payload;

import com.adadapted.sdk.addit.core.common.Command;
import com.adadapted.sdk.addit.core.device.DeviceInfo;

/**
 * Created by chrisweeden on 2/9/17.
 */

public class PickupPayloadCommand extends Command {
    private final DeviceInfo deviceInfo;

    public PickupPayloadCommand(final DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
}
