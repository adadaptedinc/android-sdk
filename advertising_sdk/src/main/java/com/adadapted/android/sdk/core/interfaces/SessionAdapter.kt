package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.session.Session

interface SessionAdapter {
    suspend fun sendInit(deviceInfo: DeviceInfo, listener: SessionInitListener)
    suspend fun sendRefreshAds(session: Session, listener: AdGetListener)
}