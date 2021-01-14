package com.adadapted.android.sdk.config

object Config {
    private var isProd = false

    const val DEFAULT_AD_POLLING = 300000L // If the new Ad polling isn't set it will default to every 5 minutes
    const val DEFAULT_EVENT_POLLING = 2500L // Events will be pushed to the server every 2.5 seconds
    const val DEFAULT_AD_REFRESH = 6000L // If an Ad does not have a refresh time it will default to 60 seconds

    const val AASDK_PREFS_KEY = "AASDK_PREFS"
    const val AASDK_PREFS_TRACKING_DISABLED_KEY = "TRACKING_DISABLED"

    private const val AD_SERVER_VERSION = "/v/0.9.5/"
    private const val TRACKING_SERVER_VERSION = "/v/1/"
    private const val PAYLOAD_SERVER_VERSION = "/v/1/"

    private const val SESSION_INIT_PATH = "android/sessions/initialize"
    private const val REFRESH_ADS_PATH = "android/ads/retrieve"
    private const val AD_EVENTS_PATH = "android/ads/events"
    private const val RETRIEVE_INTERCEPTS_PATH = "android/intercepts/retrieve"
    private const val INTERCEPT_EVENTS_PATH = "android/intercepts/events"
    private const val EVENT_TRACK_PATH = "android/events"
    private const val ERROR_TRACK_PATH = "android/errors"
    private const val PAYLOAD_PICKUP_PATH = "pickup"
    private const val PAYLOAD_TRACK_PATH = "tracking"

    fun getInitSessionUrl() = getAdServerFormattedUrl(SESSION_INIT_PATH)
    fun getRefreshAdsUrl() = getAdServerFormattedUrl(REFRESH_ADS_PATH)
    fun getAdsEventUrl() = getAdServerFormattedUrl(AD_EVENTS_PATH)
    fun getRetrieveInterceptsUrl() = getAdServerFormattedUrl(RETRIEVE_INTERCEPTS_PATH)
    fun getInterceptEventsUrl() = getAdServerFormattedUrl(INTERCEPT_EVENTS_PATH)
    fun getAppEventsUrl() = getTrackingServerFormattedUrl(EVENT_TRACK_PATH)
    fun getAppErrorsUrl() = getTrackingServerFormattedUrl(ERROR_TRACK_PATH)
    fun getPickupPayloadsUrl() = getPayloadServerFormattedUrl(PAYLOAD_PICKUP_PATH)
    fun getTrackingPayloadUrl() = getPayloadServerFormattedUrl(PAYLOAD_TRACK_PATH)

    fun init(useProd: Boolean) {
        isProd = useProd
    }

    private fun getAdServerHost(): String {
        return if (isProd) Prod.AD_SERVER_HOST else Sand.AD_SERVER_HOST
    }

    private fun getEventCollectorHost(): String {
        return if (isProd) Prod.EVENT_COLLECTOR_HOST else Sand.EVENT_COLLECTOR_HOST
    }

    private fun getPayloadHost(): String {
        return if (isProd) Prod.PAYLOAD_HOST else Sand.PAYLOAD_HOST
    }

    private fun getAdServerFormattedUrl(path: String): String {
        return getAdServerHost().plus(AD_SERVER_VERSION).plus(path)
    }

    private fun getTrackingServerFormattedUrl(path: String): String {
        return getEventCollectorHost().plus(TRACKING_SERVER_VERSION).plus(path)
    }

    private fun getPayloadServerFormattedUrl(path: String): String {
        return getPayloadHost().plus(PAYLOAD_SERVER_VERSION).plus(path)
    }

    internal object Prod {
        const val AD_SERVER_HOST = "https://ads.adadapted.com"
        const val EVENT_COLLECTOR_HOST = "https://ec.adadapted.com"
        const val PAYLOAD_HOST = "https://payload.adadapted.com"
    }

    internal object Sand {
        const val AD_SERVER_HOST = "https://sandbox.adadapted.com"
        const val EVENT_COLLECTOR_HOST = "https://sandec.adadapted.com"
        const val PAYLOAD_HOST = "https://sandpayload.adadapted.com"
    }
}