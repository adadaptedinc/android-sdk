package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import java.util.Set;

public interface AppEventSink {
    void generateWrappers(DeviceInfo deviceInfo);
    void publishEvent(Set<AppEvent> events);
    void publishError(Set<AppError> errors);
}
