package com.adadapted.android.sdk.ui.listener;

import com.adadapted.android.sdk.ui.model.ContentPayload;

/**
 * Created by chrisweeden on 7/24/15.
 */
public interface AaContentListener {
    void onContentAvailable(String zoneId, ContentPayload contentPayload);
}
