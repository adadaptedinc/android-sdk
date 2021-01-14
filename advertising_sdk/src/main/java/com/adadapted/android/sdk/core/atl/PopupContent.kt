package com.adadapted.android.sdk.core.atl

import com.adadapted.android.sdk.core.atl.AddToListContent.Sources
import com.adadapted.android.sdk.core.atl.PopupClient.markPopupContentAcknowledged
import com.adadapted.android.sdk.core.atl.PopupClient.markPopupContentFailed
import com.adadapted.android.sdk.core.atl.PopupClient.markPopupContentItemAcknowledged
import com.adadapted.android.sdk.core.atl.PopupClient.markPopupContentItemFailed
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class PopupContent(val payloadId: String, private val items: List<AddToListItem>) : AddToListContent {
    private var handled = false
    private val lock: Lock = ReentrantLock()

    override fun acknowledge() {
        lock.lock()
        try {
            if (!handled) {
                handled = true
                markPopupContentAcknowledged(this)
            }
        } finally {
            lock.unlock()
        }
    }

    override fun itemAcknowledge(item: AddToListItem) {
        lock.lock()
        try {
            if (!handled) {
                handled = true
                markPopupContentAcknowledged(this)
            }
            markPopupContentItemAcknowledged(this, item)
        } finally {
            lock.unlock()
        }
    }

    override fun failed(message: String) {
        lock.lock()
        try {
            if (!handled) {
                handled = true
                markPopupContentFailed(this, message)
            }
        } finally {
            lock.unlock()
        }
    }

    override fun itemFailed(item: AddToListItem, message: String) {
        lock.lock()
        try {
            markPopupContentItemFailed(this, item, message)
        } finally {
            lock.unlock()
        }
    }

    override fun getSource(): String {
        return Sources.IN_APP
    }

    override fun getItems(): List<AddToListItem> {
        return items
    }

    override fun hasNoItems(): Boolean {
        return items.isEmpty()
    }

    companion object {
        fun createPopupContent(payloadId: String, items: List<AddToListItem>): PopupContent {
            return PopupContent(payloadId, items)
        }
    }
}