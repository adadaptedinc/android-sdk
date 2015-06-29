package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;

/**
 * Created by chrisweeden on 6/23/15.
 */
public interface KeywordInterceptRequestBuilder<T> {
    T buildInitRequest(Session session, DeviceInfo deviceInfo);
    T buildTrackRequest(DeviceInfo deviceInfo, String sessionId, String term, String userInput,
                        String eventType);
}
