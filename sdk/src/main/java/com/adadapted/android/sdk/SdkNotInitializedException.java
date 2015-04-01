package com.adadapted.android.sdk;

/**
 * Created by chrisweeden on 3/24/15.
 */
class SdkNotInitializedException extends Exception {
    SdkNotInitializedException() {}

    SdkNotInitializedException(String detailMessage) {
        super(detailMessage);
    }

    SdkNotInitializedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    SdkNotInitializedException(Throwable throwable) {
        super(throwable);
    }
}
