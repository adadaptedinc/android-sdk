package com.adadapted.android.sdk.core.concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Transporter: TransporterCoroutineScope {
    override fun dispatchToBackground(backgroundFunc: suspend CoroutineScope.() -> Unit): Job {
        return launch {
            backgroundFunc()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}