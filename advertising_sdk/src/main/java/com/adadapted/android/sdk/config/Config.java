package com.adadapted.android.sdk.config;

public class Config {
    public static final String SDK_VERSION = "2.0.0";
    public static final long DEFAULT_AD_POLLING = 300000L; // If the new Ad polling isn't set it will default to every 5 minutes
    public static final long DEFAULT_EVENT_POLLING = 2500L; // Events will be pushed to the server every 2.5 seconds
    public static final long DEFAULT_AD_REFRESH = 60L; // If an Ad does not have a refresh time it will default to 60 seconds

    public static class Prod {
        static final String ADSERVER_HOST = "https://ads.adadapted.com";
        static final String EVENTCOLLECTOR_HOST = "https://ec.adadapted.com";
        static final String PAYLOAD_HOST = "https://payload.adadapted.com";
    }

    public static class Sand {
//        private static final String HOST = "https://sandbox.adadapted.com";
//        private static final String EVENTCOLLECTOR_HOST = "https://sandec.adadapted.com";
//        private static final String PAYLOAD_HOST = "https://sandpayload.adadapted.com";

        static final String ADSERVER_HOST = "http://10.100.131.183:8081";
        static final String EVENTCOLLECTOR_HOST = "http://10.100.131.183:8084";
        static final String PAYLOAD_HOST = "http://10.100.131.183:8085";
    }

    private static final String SESSION_INIT_PATH = "/v/0.9.5/android/sessions/initialize";
    private static final String REFRESH_ADS_PATH = "/v/0.9.5/android/ads/retrieve";
    private static final String AD_EVENTS_PATH = "/v/0.9.5/android/ads/events";
    private static final String RETRIEVE_INTERCEPTS_PATH = "/v/0.9.5/ios/intercepts/retrieve";
    private static final String INTERCEPT_EVENTS_PATH = "/v/0.9.5/android/intercepts/events";

    private static final String EVENT_TRACK_PATH = "/v/1/android/events";
    private static final String ERROR_TRACK_PATH = "/v/1/android/errors";

    private static final String PAYLOAD_PICKUP_PATH = "/v/1/pickup";
    private static final String PAYLOAD_TRACK_PATH = "/v/1/tracking";

    public static String initializeSessionUrl(final boolean isProd) {
        final String url = isProd ? Prod.ADSERVER_HOST : Sand.ADSERVER_HOST;
        return url.concat(SESSION_INIT_PATH);

    }

    public static String refreshAdsUrl(final boolean isProd) {
        final String url = isProd ? Prod.ADSERVER_HOST : Sand.ADSERVER_HOST;
        return url.concat(REFRESH_ADS_PATH);
    }

    public static String adEventsUrl(final boolean isProd) {
        final String url = isProd ? Prod.ADSERVER_HOST : Sand.ADSERVER_HOST;
        return url.concat(AD_EVENTS_PATH);
    }

    public static String retrieveInterceptsUrl(final boolean isProd) {
        final String url = isProd ? Prod.ADSERVER_HOST : Sand.ADSERVER_HOST;
        return url.concat(RETRIEVE_INTERCEPTS_PATH);
    }

    public static String interceptEventsUrl(final boolean isProd) {
        final String url = isProd ? Prod.ADSERVER_HOST : Sand.ADSERVER_HOST;
        return url.concat(INTERCEPT_EVENTS_PATH);
    }

    public static String appEventsUrl(final boolean isProd) {
        final String url = isProd ? Prod.EVENTCOLLECTOR_HOST : Sand.EVENTCOLLECTOR_HOST;
        return url.concat(EVENT_TRACK_PATH);
    }

    public static String appErrorsUrl(final boolean isProd) {
        final String url = isProd ? Prod.EVENTCOLLECTOR_HOST : Sand.EVENTCOLLECTOR_HOST;
        return url.concat(ERROR_TRACK_PATH);
    }

    public static String pickupPayloadsUrl(final boolean isProd) {
        final String url = isProd ? Prod.PAYLOAD_HOST : Sand.PAYLOAD_HOST;
        return url.concat(PAYLOAD_PICKUP_PATH);
    }

    public static String trackPayloadUrl(final boolean isProd) {
        final String url = isProd ? Prod.PAYLOAD_HOST : Sand.PAYLOAD_HOST;
        return url.concat(PAYLOAD_TRACK_PATH);
    }

}
