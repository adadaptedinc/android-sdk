package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.Session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 4/1/15.
 */
public interface AdRequestBuilder {
    JSONObject buildAdRequest(DeviceInfo deviceInfo, Session session);
}
