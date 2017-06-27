package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.AdContentPayload;

import java.util.HashSet;
import java.util.Set;

public class SdkContentPublisher {
    private static SdkContentPublisher sSdkContentPublisher;

    public static synchronized SdkContentPublisher getInstance() {
        if(sSdkContentPublisher == null) {
            sSdkContentPublisher = new SdkContentPublisher();
        }

        return sSdkContentPublisher;
    }

    private final Set<AaSdkContentListener> mListeners;

    private SdkContentPublisher() {
        mListeners = new HashSet<>();
    }

    public void addListener(final AaSdkContentListener listener) {
        if(listener != null) {
            mListeners.add(listener);
        }
    }

    public void removeListener(final AaSdkContentListener listener) {
        if(listener != null) {
            mListeners.remove(listener);
        }
    }

    public void publishContent(final String zoneId,
                               final AdContentPayload payload) {
        for(final AaSdkContentListener listener : mListeners) {
            listener.onContentAvailable(zoneId, payload);
        }
    }
}
