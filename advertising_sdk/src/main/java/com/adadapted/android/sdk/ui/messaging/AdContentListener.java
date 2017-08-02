package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.AdContent;

public interface AdContentListener {
    void onContentAvailable(String zoneId, AdContent content);
}
