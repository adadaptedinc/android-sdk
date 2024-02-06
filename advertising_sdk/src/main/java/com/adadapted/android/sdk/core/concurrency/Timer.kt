package com.adadapted.android.sdk.core.concurrency

import kotlinx.coroutines.*

class Timer(timedBackgroundFunc: () -> Unit, repeatMillis: Long, delayMillis: Long = 0) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long, action: () -> Unit) =
        scope.launch(Dispatchers.Main) {
            delay(delayMillis)
            if (repeatMillis > 0) {
                while (true) {
                    action()
                    delay(repeatMillis)
                }
            } else {
                action()
            }
        }

    private val timer: Job = startCoroutineTimer(delayMillis, repeatMillis) {
        timedBackgroundFunc()
    }

    fun startTimer() {
        timer.start()
    }

    fun cancelTimer() {
        timer.cancel()
    }
}