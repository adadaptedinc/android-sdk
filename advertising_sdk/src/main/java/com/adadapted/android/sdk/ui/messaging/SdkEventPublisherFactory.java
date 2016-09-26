package com.adadapted.android.sdk.ui.messaging;

/**
 * Created by chrisweeden on 8/18/15.
 */
public class SdkEventPublisherFactory {
    private static SdkEventPublisher sPublisherManager;

    public static SdkEventPublisher getSdkEventPublisher() {
        if(sPublisherManager == null) {
            sPublisherManager = new SdkEventPublisher();
        }

        return sPublisherManager;
    }
}
