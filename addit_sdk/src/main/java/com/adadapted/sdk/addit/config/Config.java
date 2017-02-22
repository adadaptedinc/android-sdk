package com.adadapted.sdk.addit.config;

/**
 * Created by chrisweeden on 9/26/16.
 */
public final class Config {
    public static final String SDK_VERSION = "0.3.1";

    public static class Prod {
        public static final String URL_APP_ERROR_TRACK = "https://ec.adadapted.com/v/1/android/errors";
        public static final String URL_APP_EVENT_TRACK = "https://ec.adadapted.com/v/1/android/events";

        public static final String URL_APP_PAYLOAD_PICKUP = "https://payload.adadapted.com/v/1/pickup";
        public static final String URL_APP_PAYLOAD_TRACK = "https://payload.adadapted.com/v/1/tracking";
    }

    public static class Dev {
        public static final String URL_APP_ERROR_TRACK = "https://sandec.adadapted.com/v/1/android/errors";
        public static final String URL_APP_EVENT_TRACK = "https://sandec.adadapted.com/v/1/android/events";

        public static final String URL_APP_PAYLOAD_PICKUP = "https://sandpayload.adadapted.com/v/1/pickup";
        public static final String URL_APP_PAYLOAD_TRACK = "https://sandpayload.adadapted.com/v/1/tracking";
    }
}
