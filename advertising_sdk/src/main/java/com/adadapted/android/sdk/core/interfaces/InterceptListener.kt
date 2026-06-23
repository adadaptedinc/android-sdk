package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.keyword.InterceptData

interface InterceptListener {
    fun onKeywordInterceptInitialized(intercept: InterceptData)
}