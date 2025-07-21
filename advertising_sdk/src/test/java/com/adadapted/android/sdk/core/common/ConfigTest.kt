package com.adadapted.android.sdk.core.common

import com.adadapted.android.sdk.constants.Config
import junit.framework.Assert.assertEquals
import org.junit.Test

class ConfigTest {
    private val adServerVersion = "/v/0.9.5/"
    private val trackServerVersion = "/v/1/"

    @Test
    fun serverUrlsUseSandboxByDefault() {
        assertEquals(ServerUrl.SAND_AD_SERVER.plus(adServerVersion).plus("android/sessions/initialize"), Config.getInitSessionUrl())
        assertEquals(ServerUrl.SAND_AD_SERVER.plus(adServerVersion).plus("android/ads/retrieve"), Config.getRetrieveAdsUrl())
        assertEquals(ServerUrl.SAND_AD_SERVER.plus(adServerVersion).plus("android/ads/events"), Config.getAdEventsUrl())
        assertEquals(ServerUrl.SAND_AD_SERVER.plus(adServerVersion).plus("android/intercepts/retrieve"), Config.getRetrieveInterceptsUrl())
        assertEquals(ServerUrl.SAND_AD_SERVER.plus(adServerVersion).plus("android/intercepts/events"), Config.getInterceptEventsUrl())
        assertEquals(ServerUrl.SAND_EVENT_SERVER.plus(trackServerVersion).plus("android/events"), Config.getSdkEventsUrl())
        assertEquals(ServerUrl.SAND_EVENT_SERVER.plus(trackServerVersion).plus("android/errors"), Config.getSdkErrorsUrl())
        assertEquals(ServerUrl.SAND_PAYLOAD_SERVER.plus(trackServerVersion).plus("pickup"), Config.getPickupPayloadsUrl())
        assertEquals(ServerUrl.SAND_PAYLOAD_SERVER.plus(trackServerVersion).plus("tracking"), Config.getTrackingPayloadUrl())
    }

    @Test
    fun settingProdUsesProdUrls() {
        Config.init(true)
        assertEquals(ServerUrl.PROD_AD_SERVER.plus(adServerVersion).plus("android/sessions/initialize"), Config.getInitSessionUrl())
        assertEquals(ServerUrl.PROD_AD_SERVER.plus(adServerVersion).plus("android/ads/retrieve"), Config.getRetrieveAdsUrl())
        assertEquals(ServerUrl.PROD_AD_SERVER.plus(adServerVersion).plus("android/ads/events"), Config.getAdEventsUrl())
        assertEquals(ServerUrl.PROD_AD_SERVER.plus(adServerVersion).plus("android/intercepts/retrieve"), Config.getRetrieveInterceptsUrl())
        assertEquals(ServerUrl.PROD_AD_SERVER.plus(adServerVersion).plus("android/intercepts/events"), Config.getInterceptEventsUrl())
        assertEquals(ServerUrl.PROD_EVENT_SERVER.plus(trackServerVersion).plus("android/events"), Config.getSdkEventsUrl())
        assertEquals(ServerUrl.PROD_EVENT_SERVER.plus(trackServerVersion).plus("android/errors"), Config.getSdkErrorsUrl())
        assertEquals(ServerUrl.PROD_PAYLOAD_SERVER.plus(trackServerVersion).plus("pickup"), Config.getPickupPayloadsUrl())
        assertEquals(ServerUrl.PROD_PAYLOAD_SERVER.plus(trackServerVersion).plus("tracking"), Config.getTrackingPayloadUrl())
    }

    internal object ServerUrl {
        const val PROD_AD_SERVER = "https://ads.adadapted.com"
        const val PROD_EVENT_SERVER = "https://ec.adadapted.com"
        const val PROD_PAYLOAD_SERVER = "https://payload.adadapted.com"
        const val SAND_AD_SERVER = "https://sandbox.adadapted.com"
        const val SAND_EVENT_SERVER = "https://sandec.adadapted.com"
        const val SAND_PAYLOAD_SERVER = "https://sandpayload.adadapted.com"
    }
}