package com.adadapted.android.sdk.tools

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.session.Session
import java.util.Date

object MockData {
    val session = Session("testId", true, true, 30, Date().time.plus(10000000), mutableMapOf())

    init {
        session.deviceInfo = DeviceInfo(isAllowRetargetingEnabled = true)
    }
}