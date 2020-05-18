package com.adadapted.android.sdk.core.ad

interface Counter {
    fun getIncrementedCountFor(impressionId: String): Int
    fun getCurrentCountFor(impressionId: String): Int
}