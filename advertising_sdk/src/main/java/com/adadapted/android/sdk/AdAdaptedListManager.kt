package com.adadapted.android.sdk

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.log.AALogger
import kotlin.jvm.Synchronized

object AdAdaptedListManager {
    private const val LIST_NAME = "list_name"
    private const val ITEM_NAME = "item_name"

    @Synchronized
    fun itemAddedToList(list: String = "", item: String,) {
        if (item.isEmpty()) {
            return
        }
        EventClient.trackSdkEvent(EventStrings.USER_ADDED_TO_LIST, generateListParams(list, item))
        AALogger.logInfo("$item was added to $list")
    }

    @Synchronized
    fun itemCrossedOffList(list: String = "", item: String) {
        if (item.isEmpty()) {
            return
        }
        EventClient.trackSdkEvent(EventStrings.USER_CROSSED_OFF_LIST, generateListParams(list, item))
        AALogger.logInfo("$item was crossed off $list")
    }

    @Synchronized
    fun itemDeletedFromList(list: String = "", item: String) {
        if (item.isEmpty()) {
            return
        }
        EventClient.trackSdkEvent(EventStrings.USER_DELETED_FROM_LIST, generateListParams(list, item))
        AALogger.logInfo("$item was deleted from $list")
    }

    private fun generateListParams(list: String, item: String): MutableMap<String, String> {
        val params = HashMap<String, String>()
        params[LIST_NAME] = list
        params[ITEM_NAME] = item
        return params
    }
}