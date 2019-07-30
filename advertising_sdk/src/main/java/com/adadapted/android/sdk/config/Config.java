package com.adadapted.android.sdk.config;

public class Config {
    public static final String SDK_VERSION = "2.0.0";
    public static final long DEFAULT_AD_POLLING = 300000L; // If the new Ad polling isn't set it will default to every 5 minutes
    public static final long DEFAULT_EVENT_POLLING = 2500L; // Events will be pushed to the server every 2.5 seconds
    public static final long DEFAULT_AD_REFRESH = 60L; // If an Ad does not have a refresh time it will default to 60 seconds

    static class Prod {
        static final String AD_SERVER_HOST = "https://ads.adadapted.com";
        static final String EVENT_COLLECTOR_HOST = "https://ec.adadapted.com";
        static final String PAYLOAD_HOST = "https://payload.adadapted.com";
    }

    static class Sand {
        static final String AD_SERVER_HOST = "https://sandbox.adadapted.com";
        static final String EVENT_COLLECTOR_HOST = "https://sandec.adadapted.com";
        static final String PAYLOAD_HOST = "https://sandpayload.adadapted.com";
    }

    private static final String SESSION_INIT_PATH = "/v/0.9.5/android/sessions/initialize";
    private static final String REFRESH_ADS_PATH = "/v/0.9.5/android/ads/retrieve";
    private static final String AD_EVENTS_PATH = "/v/0.9.5/android/ads/events";
    private static final String RETRIEVE_INTERCEPTS_PATH = "/v/0.9.5/android/intercepts/retrieve";
    private static final String INTERCEPT_EVENTS_PATH = "/v/0.9.5/android/intercepts/events";

    private static final String EVENT_TRACK_PATH = "/v/1/android/events";
    private static final String ERROR_TRACK_PATH = "/v/1/android/errors";

    private static final String PAYLOAD_PICKUP_PATH = "/v/1/pickup";
    private static final String PAYLOAD_TRACK_PATH = "/v/1/tracking";

    private static String getAdServerHost(final boolean isProd) {
        return isProd ? Prod.AD_SERVER_HOST : Sand.AD_SERVER_HOST;
    }

    private static String getEventCollectorHost(final boolean isProd) {
        return isProd ? Prod.EVENT_COLLECTOR_HOST : Sand.EVENT_COLLECTOR_HOST;
    }

    private static String getPayloadHost(final boolean isProd) {
        return isProd ? Prod.PAYLOAD_HOST : Sand.PAYLOAD_HOST;
    }

    public static String initializeSessionUrl(final boolean isProd) {
        return getAdServerHost(isProd).concat(SESSION_INIT_PATH);
    }

    public static String refreshAdsUrl(final boolean isProd) {
        return getAdServerHost(isProd).concat(REFRESH_ADS_PATH);
    }

    public static String adEventsUrl(final boolean isProd) {
        return getAdServerHost(isProd).concat(AD_EVENTS_PATH);
    }

    public static String retrieveInterceptsUrl(final boolean isProd) {
        return getAdServerHost(isProd).concat(RETRIEVE_INTERCEPTS_PATH);
    }

    public static String interceptEventsUrl(final boolean isProd) {
        return getAdServerHost(isProd).concat(INTERCEPT_EVENTS_PATH);
    }

    public static String appEventsUrl(final boolean isProd) {
        return getEventCollectorHost(isProd).concat(EVENT_TRACK_PATH);
    }

    public static String appErrorsUrl(final boolean isProd) {
        return getEventCollectorHost(isProd).concat(ERROR_TRACK_PATH);
    }

    public static String pickupPayloadsUrl(final boolean isProd) {
        return getPayloadHost(isProd).concat(PAYLOAD_PICKUP_PATH);
    }

    public static String trackPayloadUrl(final boolean isProd) {
        return getPayloadHost(isProd).concat(PAYLOAD_TRACK_PATH);
    }
}
