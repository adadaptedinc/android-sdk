package com.adadapted.android.sdk.core.event

interface BaseAppEventClient {
    fun trackSdkEvent(name: String, params: Map<String, String>)
    fun trackSdkEvent(name: String)
    fun trackError(code: String, message: String, params: Map<String, String>)
    fun trackError(code: String, message: String)
    fun trackAppEvent(name: String, params: Map<String, String>)
    fun trackAppEvent(name: String)
    fun publishEvents()
}