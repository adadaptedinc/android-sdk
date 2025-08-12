package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.log.AALogger

object HttpErrorTracker {
    fun trackHttpError(errorCause: String, errorMessage: String, errorEventCode: String, url: String) {
        val params: MutableMap<String, String> = HashMap()
        params["url"] = url
        params["data"] = errorCause
        try {
            EventClient.trackSdkError(errorEventCode, errorMessage, params)
        } catch (illegalArg: IllegalArgumentException) {
            AALogger.logError("EventClient was not initialized, is your API key valid? DETAIL: " + illegalArg.message)
        }
    }
}