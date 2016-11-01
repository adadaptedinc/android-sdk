package com.adadapted.sdk.addit.config;

/**
 * Created by chrisweeden on 9/26/16.
 */
public final class Config {
    public static final String SDK_VERSION = "0.2.0";

    public static class Prod {
        public static final String URL_APP_ERROR_TRACK = "https://ec.adadapted.com/v/1/android/errors";
        public static final String URL_APP_EVENT_TRACK = "https://ec.adadapted.com/v/1/android/events";
    }

    public static class Dev {
        public static final String URL_APP_ERROR_TRACK = "https://sandec.adadapted.com/v/1/android/errors";
        public static final String URL_APP_EVENT_TRACK = "https://sandec.adadapted.com/v/1/android/events";
    }
}
