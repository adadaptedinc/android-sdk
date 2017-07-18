package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.core.addit.Content;

import java.util.HashMap;
import java.util.Map;

public class AdditContentPublisher {
    private static AdditContentPublisher sInstance;

    public static AdditContentPublisher getInstance() {
        if(sInstance == null) {
            sInstance = new AdditContentPublisher();
        }

        return sInstance;
    }

    private final Map<String, Content> publishedContent;
    private AaSdkAdditContentListener mListener;

    private AdditContentPublisher() {
        publishedContent = new HashMap<>();
    }

    public void addListener(final AaSdkAdditContentListener listener) {
        if(listener != null) {
            mListener = listener;
        }
    }

    public void publishContent(final Content content) {
        if(content == null) {
            return;
        }

        if(publishedContent.containsKey(content.getPayloadId())) {
            content.duplicate();
            return;
        }

        if(mListener != null) {
            publishedContent.put(content.getPayloadId(), content);
            mListener.onContentAvailable(content);
        }
    }
}
