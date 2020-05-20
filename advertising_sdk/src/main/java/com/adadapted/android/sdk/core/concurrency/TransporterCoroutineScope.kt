package com.adadapted.android.sdk.core.concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface TransporterCoroutineScope : CoroutineScope {
    fun dispatchToBackground(backgroundFunc: suspend CoroutineScope.() -> Unit) : Job
}