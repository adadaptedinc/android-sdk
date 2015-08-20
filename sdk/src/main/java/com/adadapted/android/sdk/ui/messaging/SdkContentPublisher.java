package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.ui.model.ContentPayload;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 8/18/15.
 */
public class SdkContentPublisher {
    private Set<AaSdkContentListener> mListeners;

    public SdkContentPublisher() {
        mListeners = new HashSet<>();
    }

    public void addListener(AaSdkContentListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(AaSdkContentListener listener) {
        mListeners.remove(listener);
    }

    public void publishContent(String zoneId, ContentPayload payload) {
        for(AaSdkContentListener listener : mListeners) {
            listener.onContentAvailable(zoneId, payload);
        }
    }
}
