package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONObject;

import java.util.Map;

public interface AppErrorBuilder {
    JSONObject buildWrapper(DeviceInfo deviceInfo);
    JSONObject buildItem(JSONObject errorWrapper,
                         String errorCode,
                         String errorMessage,
                         Map<String, String> params);
}
