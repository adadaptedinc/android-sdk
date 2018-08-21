package com.adadapted.android.sdk.core.addit;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import java.util.List;

public interface PayloadAdapter {
    interface Callback {
        void onSuccess(List<AdditContent> content);
    }

    void pickup(DeviceInfo deviceInfo, Callback callback);
    void publishEvent(PayloadEvent event);
}
