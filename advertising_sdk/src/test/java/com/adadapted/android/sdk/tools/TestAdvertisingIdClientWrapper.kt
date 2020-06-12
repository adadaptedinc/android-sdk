package com.adadapted.android.sdk.tools

import android.content.Context
import com.adadapted.android.sdk.core.device.AdvertisingIdClientWrapper
import com.google.android.gms.ads.identifier.AdvertisingIdClient

class TestAdvertisingIdClientWrapper : AdvertisingIdClientWrapper() {
    override fun requestAdvertisingIdInfo(context: Context?): AdvertisingIdClient.Info? {
        return null
    }
}