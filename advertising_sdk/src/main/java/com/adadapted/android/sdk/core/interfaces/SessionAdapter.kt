package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.view.ZoneContext

interface SessionAdapter {
    suspend fun sendInit(deviceInfo: DeviceInfo, listener: SessionInitListener)
    suspend fun sendRefreshAds(session: Session, listener: AdGetListener, zoneContexts: MutableSet<ZoneContext>)
}