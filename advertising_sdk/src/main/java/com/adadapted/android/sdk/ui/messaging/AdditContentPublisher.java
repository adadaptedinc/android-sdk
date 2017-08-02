package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.core.addit.Content;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AdditContentPublisher {
    private static AdditContentPublisher sInstance;

    public static AdditContentPublisher getInstance() {
        if(sInstance == null) {
            sInstance = new AdditContentPublisher();
        }

        return sInstance;
    }

    private final Map<String, Content> publishedContent;
    private AaSdkAdditContentListener listener;
    private final Lock lock = new ReentrantLock();

    private AdditContentPublisher() {
        publishedContent = new HashMap<>();
    }

    public void addListener(final AaSdkAdditContentListener listener) {
        lock.lock();
        try {
            if (listener != null) {
                this.listener = listener;
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void publishContent(final Content content) {
        if(content == null) {
            return;
        }

        lock.lock();
        try {
            if(publishedContent.containsKey(content.getPayloadId())) {
                content.duplicate();
            }
            else if(listener != null) {
                publishedContent.put(content.getPayloadId(), content);
                listener.onContentAvailable(content);
            }
        }
        finally {
            lock.unlock();
        }
    }
}
