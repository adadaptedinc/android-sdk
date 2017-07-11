package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.session.Session;

public interface AdAnomalySink {
    void sendBatch(final Session session,
                   final String adId,
                   final String eventPath,
                   final String code,
                   final String message);
}
