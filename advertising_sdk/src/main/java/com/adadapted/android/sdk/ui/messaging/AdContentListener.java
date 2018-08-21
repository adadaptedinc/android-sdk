package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.core.atl.AddToListContent;

public interface AdContentListener {
    void onContentAvailable(String zoneId, AddToListContent content);
}
