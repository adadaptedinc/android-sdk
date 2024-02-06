package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.keyword.Intercept

interface InterceptListener {
    fun onKeywordInterceptInitialized(intercept: Intercept)
}