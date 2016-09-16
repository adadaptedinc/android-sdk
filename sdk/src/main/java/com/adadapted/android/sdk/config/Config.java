package com.adadapted.android.sdk.config;

/**
 * Created by chrisweeden on 7/27/15.
 */
public class Config {
    public static final String SDK_VERSION = "0.10.0";
    public static final long DEFAULT_EVENT_POLLING = 20000L;

    public static class Prod {
        public static final String URL_SESSION_INIT = "https://ads.adadapted.com/v/0.9.3/android/session/init";
        public static final String URL_EVENT_BATCH = "https://ads.adadapted.com/v/0.9.3/android/event/batch";
        public static final String URL_AD_GET = "https://ads.adadapted.com/v/0.9.3/android/ad/get";
        public static final String URL_KI_INIT = "https://ads.adadapted.com/v/0.9.3/android/ki/init";
        public static final String URL_KI_TRACK = "https://ads.adadapted.com/v/0.9.3/android/ki/track";
        public static final String URL_CONTENT_TRACK = "https://ads.adadapted.com/v/0.9.3/content/track";

        public static final String URL_APP_EVENT_TRACK = "";
    }

    public static class Sand {
        public static final String URL_SESSION_INIT = "https://sandbox.adadapted.com/v/0.9.3/android/session/init";
        public static final String URL_EVENT_BATCH = "https://sandbox.adadapted.com/v/0.9.3/android/event/batch";
        public static final String URL_AD_GET = "https://sandbox.adadapted.com/v/0.9.3/android/ad/get";
        public static final String URL_KI_INIT = "https://sandbox.adadapted.com/v/0.9.3/android/ki/init";
        public static final String URL_KI_TRACK = "https://sandbox.adadapted.com/v/0.9.3/android/ki/track";
        public static final String URL_CONTENT_TRACK = "https://sandbox.adadapted.com/v/0.9.3/content/track";

        public static final String URL_APP_EVENT_TRACK = "http://eventcollector.t.adadapted.com/v/1/android/events";
    }
}
