package com.adadapted.android.sdk.core.concurrency

import android.os.SystemClock
import java.util.concurrent.TimeUnit

internal fun nowInSeconds(): Long = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

internal fun uptimeSeconds(): Long = TimeUnit.MILLISECONDS.toSeconds(SystemClock.uptimeMillis())
