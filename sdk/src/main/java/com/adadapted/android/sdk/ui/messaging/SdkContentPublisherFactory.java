package com.adadapted.android.sdk.ui.messaging;

/**
 * Created by chrisweeden on 8/19/15.
 */
public class SdkContentPublisherFactory {
    private static SdkContentPublisher sSdkContentPublisher;

    public static synchronized SdkContentPublisher getContentPublisher() {
        if(sSdkContentPublisher == null) {
            sSdkContentPublisher = new SdkContentPublisher();
        }

        return sSdkContentPublisher;
    }
}
