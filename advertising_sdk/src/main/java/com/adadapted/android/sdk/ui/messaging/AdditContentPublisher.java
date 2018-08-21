package com.adadapted.android.sdk.ui.messaging;

import android.os.Handler;
import android.os.Looper;

import com.adadapted.android.sdk.core.addit.AdditContent;
import com.adadapted.android.sdk.core.atl.AddToListContent;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.ad.AdContent;

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

    private final Map<String, AdditContent> publishedContent;
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

    public void publishAdditContent(final AdditContent content) {
        if(content == null || content.hasNoItems()) {
            return;
        }

        lock.lock();
        try {
            if (listener == null) {
                AppEventClient.trackError(
                    "NO_ADDIT_CONTENT_LISTENER",
                    "App did not register an Addit AdditContent listener"
                );
                return;
            }

            if(publishedContent.containsKey(content.getPayloadId())) {
                content.duplicate();
            }
            else if(listener != null) {
                publishedContent.put(content.getPayloadId(), content);
                notifyContentAvailable(content);
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void publishAdContent(final AdContent content) {
        if(content == null || content.hasNoItems()) {
            return;
        }

        lock.lock();
        try {
            if (listener == null) {
                AppEventClient.trackError(
                    "NO_ADDIT_CONTENT_LISTENER",
                    "App did not register an Addit AdditContent listener"
                );
                return;
            }

            notifyContentAvailable(content);
        }
        finally {
            lock.unlock();
        }
    }

    private void notifyContentAvailable(final AddToListContent content) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                listener.onContentAvailable(content);
            }
        });
    }
}
