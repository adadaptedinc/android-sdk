package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.core.addit.AdditContent;

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

    private AaSdkAdditContentListener mListener;

    private AdditContentPublisher() {}

    public void addListener(final AaSdkAdditContentListener listener) {
        if(listener != null) {
            mListener = listener;
        }
    }

    public void publishContent(final AdditContent payload) {
        if(mListener != null && payload != null) {
            mListener.onContentAvailable(payload);
        }
    }
}
