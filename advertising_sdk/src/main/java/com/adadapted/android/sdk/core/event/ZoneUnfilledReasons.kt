package com.adadapted.android.sdk.core.event

object ZoneUnfilledReasons {
    const val NO_AD = "no_ad" //The server answered fine and had nothing to serve
    const val REQUEST_FAILED = "request_failed" //The ad request never came back with a response
    const val RENDER_FAILED = "render_failed" //An ad was served and the WebView could not show it
}
