package com.adadapted.android.sdk.core.anomaly;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/23/16.
 */

public interface AnomalyBuilder {
    JSONObject build(final Session session,
                     final String adId,
                     final String eventPath,
                     final String code,
                     final String message);
}
