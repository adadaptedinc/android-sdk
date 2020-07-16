package com.adadapted.android.sdk.ui.messaging

import android.os.Handler
import android.os.Looper
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.AdContent
import com.adadapted.android.sdk.core.addit.AdditContent
import com.adadapted.android.sdk.core.atl.AddToListContent
import com.adadapted.android.sdk.core.atl.PopupContent
import com.adadapted.android.sdk.core.event.AppEventClient
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class AdditContentPublisher private constructor() {
    private val publishedContent: MutableMap<String, AdditContent> = HashMap()
    private var listener: AaSdkAdditContentListener? = null
    private val lock: Lock = ReentrantLock()

    fun addListener(listener: AaSdkAdditContentListener) {
        lock.lock()
        try {
            this.listener = listener
        } finally {
            lock.unlock()
        }
    }

    fun publishAdditContent(content: AdditContent) {
        if (content.hasNoItems()) {
            return
        }
        lock.lock()
        try {
            if (listener == null) {
                AppEventClient.getInstance().trackError(EventStrings.NO_ADDIT_CONTENT_LISTENER, LISTENER_REGISTRATION_ERROR)
                return
            }
            if (publishedContent.containsKey(content.payloadId)) {
                content.duplicate()
            } else if (listener != null) {
                publishedContent[content.payloadId] = content
                notifyContentAvailable(content)
            }
        } finally {
            lock.unlock()
        }
    }

    fun publishPopupContent(content: PopupContent) {
        if (content.hasNoItems()) {
            return
        }
        lock.lock()
        try {
            if (listener == null) {
                AppEventClient.getInstance().trackError(EventStrings.NO_ADDIT_CONTENT_LISTENER, LISTENER_REGISTRATION_ERROR)
                return
            }
            notifyContentAvailable(content)
        } finally {
            lock.unlock()
        }
    }

    fun publishAdContent(content: AdContent) {
        if (content.hasNoItems()) {
            return
        }
        lock.lock()
        try {
            if (listener == null) {
                AppEventClient.getInstance().trackError(EventStrings.NO_ADDIT_CONTENT_LISTENER, LISTENER_REGISTRATION_ERROR)
                return
            }
            notifyContentAvailable(content)
        } finally {
            lock.unlock()
        }
    }

    private fun notifyContentAvailable(content: AddToListContent) {
        Handler(Looper.getMainLooper()).post { listener?.onContentAvailable(content) }
    }

    companion object {
        private const val LISTENER_REGISTRATION_ERROR = "App did not register an Addit Content listener"
        private lateinit var instance: AdditContentPublisher

        fun getInstance(): AdditContentPublisher {
            if (!this::instance.isInitialized) {
                createInstance()
            }
            return instance
        }

        fun createInstance() {
            instance = AdditContentPublisher()
        }
    }
}