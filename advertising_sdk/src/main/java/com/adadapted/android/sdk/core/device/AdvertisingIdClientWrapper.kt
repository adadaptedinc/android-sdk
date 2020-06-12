package com.adadapted.android.sdk.core.device

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient

open class AdvertisingIdClientWrapper {
    open fun requestAdvertisingIdInfo(context: Context?): AdvertisingIdClient.Info? {
        return AdvertisingIdClient.getAdvertisingIdInfo(context)
    }
}