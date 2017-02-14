package com.adadapted.sdk.addit.ui;

import com.adadapted.sdk.addit.core.content.Content;

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

    private AdditContentListener mListener;

    private AdditContentPublisher() {}

    public void addListener(final AdditContentListener listener) {
        if(listener != null) {
            mListener = listener;
        }
    }

    public void publishContent(final Content content) {
        if(mListener != null && content != null) {
            mListener.onContentAvailable(content);
        }
    }
}
