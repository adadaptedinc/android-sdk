package com.adadapted.android.sdk.core.concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface TransporterCoroutineScope: CoroutineScope {
    fun dispatchToThread(func: suspend CoroutineScope.() -> Unit): Job {
        return launch(Dispatchers.Default) {
            func()
        }
    }

    fun dispatchToMain(func: suspend CoroutineScope.() -> Unit): Job {
        return launch(Dispatchers.Main) {
            func()
        }
    }
}
