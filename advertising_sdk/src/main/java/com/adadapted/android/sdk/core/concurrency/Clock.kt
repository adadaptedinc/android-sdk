package com.adadapted.android.sdk.core.concurrency

import java.util.concurrent.TimeUnit

internal fun nowInSeconds(): Long = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
