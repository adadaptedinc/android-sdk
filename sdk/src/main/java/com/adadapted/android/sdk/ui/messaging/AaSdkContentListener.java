package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.ContentPayload;

/**
 * Created by chrisweeden on 8/18/15.
 */
public interface AaSdkContentListener {
    void onContentAvailable(String zoneId, ContentPayload payload);
}
