package com.adadapted.sdk.addit.ui;

import com.adadapted.sdk.addit.core.content.Content;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class AdditContentPublisher {
    private static AdditContentPublisher sInstance;

    public static AdditContentPublisher getInstance() {
        if(sInstance == null) {
            sInstance = new AdditContentPublisher();
        }

        return sInstance;
    }

    private final Map<String, Content> publishedContent;
    private AdditContentListener mListener;

    private AdditContentPublisher() {
        publishedContent = new HashMap<>();
    }

    public void addListener(final AdditContentListener listener) {
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
