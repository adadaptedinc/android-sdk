package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.ad.Ad

class AdAdaptedEventClient private constructor(private val adEventClient: BaseAdEventClient, private val appEventClient: BaseAppEventClient): BaseEventClient {

    override fun trackInteraction(ad: Ad) {
       adEventClient.trackInteraction(ad)
    }

    override fun trackSdkEvent(name: String, params: Map<String, String>) {
        appEventClient.trackSdkEvent(name, params)
    }

    override fun trackError(code: String, message: String, params: Map<String, String>) {
        appEventClient.trackError(code, message, params)
    }

    //TODO other client calls


    companion object {
        private lateinit var instance: AdAdaptedEventClient

        fun createInstance(adEventClient: BaseAdEventClient, appEventClient: BaseAppEventClient) {
            this.instance = AdAdaptedEventClient(adEventClient, appEventClient)
        }

        @JvmStatic
        fun getInstance(): AdAdaptedEventClient {
            return instance
        }
    }
}