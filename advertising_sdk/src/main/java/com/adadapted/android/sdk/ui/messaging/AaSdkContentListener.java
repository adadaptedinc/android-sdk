package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.AdContentPayload;

public interface AaSdkContentListener {
    void onContentAvailable(String zoneId, AdContentPayload payload);
}
