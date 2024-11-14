package com.adadapted.android.sdk.core.view

import com.adadapted.android.sdk.core.log.AALogger

class ZoneViewComposableManager private constructor() {

    private val composableCollection: MutableList<AdadaptedComposable> = mutableListOf()

    fun addComposable(composable: AdadaptedComposable) {
        cleanupComposables(composable)
        composableCollection.add(composable)
        AALogger.logDebug("Composable Count: " + composableCollection.count())
    }

    private fun cleanupComposables(newComposable: AdadaptedComposable) {
        val affectedViewModels = composableCollection.filter { it.zoneId == newComposable.zoneId }
        affectedViewModels.forEach { it.dispose() }

        composableCollection.removeAll { it.zoneId == newComposable.zoneId }
    }

    companion object {
        val shared: ZoneViewComposableManager by lazy { ZoneViewComposableManager() }
    }
}
