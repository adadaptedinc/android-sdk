package com.adadapted.android.sdk.tools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlin.coroutines.CoroutineContext

class TestTransporter(override val coroutineContext: CoroutineContext): TransporterCoroutineScope {
    private val scope = TestCoroutineScope(coroutineContext)

    override fun dispatchToBackground(backgroundFunc: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch {
            backgroundFunc.invoke(this)
        }
    }
}