package com.adadapted.android.sdk

import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance

object AdAdaptedListManager {
    private val LOGTAG = AdAdaptedListManager::class.java.name
    private const val LIST_NAME = "list_name"
    private const val ITEM_NAME = "item_name"

    @Synchronized
    fun itemAddedToList(item: String?) {
        itemAddedToList("", item)
    }

    @Synchronized
    fun itemAddedToList(list: String, item: String?) {
        if (item == null || item.isEmpty()) {
            return
        }
        val params: MutableMap<String, String> = HashMap()
        params[LIST_NAME] = list
        params[ITEM_NAME] = item
        getInstance().trackAppEvent(EventStrings.USER_ADDED_TO_LIST, params)
        Log.i(LOGTAG, String.format("%s was added to %s", item, list))
    }

    @Synchronized
    fun itemCrossedOffList(item: String?) {
        itemCrossedOffList("", item)
    }

    @Synchronized
    fun itemCrossedOffList(list: String, item: String?) {
        if (item == null || item.isEmpty()) {
            return
        }
        val params: MutableMap<String, String> = HashMap()
        params[LIST_NAME] = list
        params[ITEM_NAME] = item
        getInstance().trackAppEvent(EventStrings.USER_CROSSED_OFF_LIST, params)
        Log.i(LOGTAG, String.format("%s was crossed off %s", item, list))
    }

    @Synchronized
    fun itemDeletedFromList(item: String?) {
        itemDeletedFromList("", item)
    }

    @Synchronized
    fun itemDeletedFromList(list: String, item: String?) {
        if (item == null || item.isEmpty()) {
            return
        }
        val params: MutableMap<String, String> = HashMap()
        params[LIST_NAME] = list
        params[ITEM_NAME] = item
        getInstance().trackAppEvent(EventStrings.USER_DELETED_FROM_LIST, params)
        Log.i(LOGTAG, String.format("%s was deleted from %s", item, list))
    }
}