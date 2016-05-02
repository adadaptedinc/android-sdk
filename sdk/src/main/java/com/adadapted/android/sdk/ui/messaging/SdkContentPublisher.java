package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.ContentPayload;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 8/18/15.
 */
public class SdkContentPublisher {
    private final Set<AaSdkContentListener> mListeners;

    public SdkContentPublisher() {
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
                               final ContentPayload payload) {
        for(AaSdkContentListener listener : mListeners) {
            listener.onContentAvailable(zoneId, payload);
        }
    }
}
