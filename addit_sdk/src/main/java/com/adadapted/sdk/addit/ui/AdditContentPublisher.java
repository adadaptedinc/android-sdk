package com.adadapted.sdk.addit.ui;

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

    public void publishContent(final AdditContent payload) {
        if(mListener != null) {
            mListener.onContentAvailable(payload);
        }
    }
}
