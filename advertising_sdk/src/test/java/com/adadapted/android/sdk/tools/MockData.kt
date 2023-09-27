package com.adadapted.android.sdk.tools

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.session.Session

object MockData {
    val session = Session("testId", true, true, 30, 1907245044, mutableMapOf())

    init {
        session.deviceInfo = DeviceInfo(isAllowRetargetingEnabled = true)
    }
}