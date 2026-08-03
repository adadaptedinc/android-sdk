package com.adadapted.android.sdk.core.concurrency

import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

internal class Timer(timedBackgroundFunc: () -> Unit, repeatSeconds: Long, delaySeconds: Long = 0) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private fun startCoroutineTimer(delaySeconds: Long = 0, repeatSeconds: Long, action: () -> Unit) =
        scope.launch(Dispatchers.Main) {
            delay(delaySeconds.seconds)
            if (repeatSeconds > 0) {
                while (true) {
                    action()
                    delay(repeatSeconds.seconds)
                }
            } else {
                action()
            }
        }

    private val timer: Job = startCoroutineTimer(delaySeconds, repeatSeconds) {
        timedBackgroundFunc()
    }

    fun startTimer() {
        timer.start()
    }

    fun cancelTimer() {
        timer.cancel()
        job.cancel()
    }
}
