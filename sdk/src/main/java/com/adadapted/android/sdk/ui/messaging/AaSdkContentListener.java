package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.AdContentPayload;

/**
 * Created by chrisweeden on 8/18/15.
 */
public interface AaSdkContentListener {
    void onContentAvailable(String zoneId, AdContentPayload payload);
}
