package com.adadapted.sdk.addit.core.config;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class Config {
    public static final String SDK_VERSION = "0.1.0";

    public static class Prod {
        public static final String URL_ANOMALY_TRACK = "https://ads.adadapted.com/v/0.9.3/anomaly/track";
        public static final String URL_APP_EVENT_TRACK = "https://eventcollector.adadapted.com/v/1/android/events";
    }

    public static class Dev {
        public static final String URL_ANOMALY_TRACK = "https://sandbox.adadapted.com/v/0.9.3/anomaly/track";
        public static final String URL_APP_EVENT_TRACK = "http://eventcollector.t.adadapted.com/v/1/android/events";
    }
}
