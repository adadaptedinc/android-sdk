package com.adadapted.android.sdk.core.log

import android.util.Log
import com.adadapted.android.sdk.constants.Config

object AALogger {
    private var isDebugLoggingEnabled = false
    private var disableLogging = false

    fun logDebug(message: String) {
        if (isDebugLoggingEnabled && !disableLogging) {
            Log.d(Config.LOG_TAG, message)
        }
    }

    fun logInfo(message: String) {
        if (!disableLogging)Log.i(Config.LOG_TAG, message)
    }

    fun logError(message: String) {
        if (!disableLogging)Log.e(Config.LOG_TAG, message)
    }

    fun enableDebugLogging() {
        isDebugLoggingEnabled = true
    }

    fun disableAllLogging() {
        disableLogging = true
    }
}