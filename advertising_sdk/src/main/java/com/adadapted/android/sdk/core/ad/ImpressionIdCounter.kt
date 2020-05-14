package com.adadapted.android.sdk.core.ad

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ImpressionIdCounter private constructor() {
    private val idCounts: MutableMap<String, Int?>
    private val counterLock: Lock = ReentrantLock()
    private fun initCountFor(impressionId: String) {
        idCounts[impressionId] = INITIAL_VAL
    }

    @Synchronized
    fun getIncrementedCountFor(impressionId: String): Int {
        counterLock.lock()
        return try {
            if (idCounts.containsKey(impressionId)) {
                var value = idCounts[impressionId]!!
                value++
                idCounts[impressionId] = value
                value
            } else {
                initCountFor(impressionId)
                idCounts[impressionId]!!
            }
        } finally {
            counterLock.unlock()
        }
    }

    @Synchronized
    fun getCurrentCountFor(impressionId: String): Int {
        counterLock.lock()
        return try {
            if (idCounts.containsKey(impressionId)) {
                idCounts[impressionId]!!
            } else {
                initCountFor(impressionId)
                idCounts[impressionId]!!
            }
        } finally {
            counterLock.unlock()
        }
    }

    companion object {
        var instance: ImpressionIdCounter? = null
            get() {
                if (field == null) {
                    field = ImpressionIdCounter()
                }
                return field
            }
            private set

        private const val INITIAL_VAL = 1
    }

    init {
        idCounts = HashMap()
    }
}