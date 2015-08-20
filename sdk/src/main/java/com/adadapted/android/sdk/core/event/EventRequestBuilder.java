package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.event.model.EventTypes;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/23/15.
 */
public interface EventRequestBuilder {
    JSONObject build(Session session, Ad ad, EventTypes eventType, String eventName);
}
