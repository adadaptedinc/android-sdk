package com.adadapted.android.sdk.core.concurrency

import android.os.SystemClock
import java.util.concurrent.TimeUnit

internal fun nowInSeconds(): Long = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

//elapsedRealtime, not uptimeMillis: it keeps counting through doze, and unlike the wall clock it cannot jump
internal fun monotonicSeconds(): Long = TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime())
