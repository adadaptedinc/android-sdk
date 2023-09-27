package com.adadapted.android.sdk.tools

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlin.coroutines.CoroutineContext

class TestTransporter(override val coroutineContext: CoroutineContext): TransporterCoroutineScope {
    private val scope = TestScope(coroutineContext)

    override fun dispatchToMain(backgroundFunc: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch {
            backgroundFunc.invoke(this)
        }
    }

    override fun dispatchToThread(backgroundFunc: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch {
            backgroundFunc.invoke(this)
        }
    }
}