package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/23/15.
 */
public interface EventRequestBuilder {
    JSONObject build(DeviceInfo deviceInfo,
                     String sessionId,
                     Ad ad,
                     EventTypes eventType,
                     String eventName);
}
