package com.adadapted.android.sdk.core.atl

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.constants.EventStrings.LISTENER_REGISTRATION_ERROR
import com.adadapted.android.sdk.core.ad.AdContent
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.AddItContentListener
import com.adadapted.android.sdk.core.log.AALogger

object AddItContentPublisher {

    private var transporter: Transporter = Transporter()
    private val publishedContent: MutableMap<String, AddItContent> = HashMap()
    private var listener: AddItContentListener? = null

    fun addListener(listener: AddItContentListener) {
        AddItContentPublisher.listener = listener
    }

    fun publishAddItContent(content: AddItContent) {
        if (content.hasNoItems()) {
            return
        }
        if (listener == null) {
            EventClient.trackSdkError(EventStrings.NO_ADDIT_CONTENT_LISTENER, LISTENER_REGISTRATION_ERROR)
            contentListenerNotAdded()
            return
        }
        if (publishedContent.containsKey(content.payloadId)) {
            content.duplicate()
        } else if (listener != null) {
            publishedContent[content.payloadId] = content
            notifyContentAvailable(content)
        }
    }

    fun publishPopupContent(content: PopupContent) {
        if (content.hasNoItems()) {
            return
        }
        if (listener == null) {
            EventClient.trackSdkError(EventStrings.NO_ADDIT_CONTENT_LISTENER, LISTENER_REGISTRATION_ERROR)
            contentListenerNotAdded()
            return
        }
        notifyContentAvailable(content)
    }

    fun publishAdContent(content: AdContent) {
        if (content.hasNoItems()) {
            return
        }
        if (listener == null) {
            EventClient.trackSdkError(EventStrings.NO_ADDIT_CONTENT_LISTENER, LISTENER_REGISTRATION_ERROR)
            contentListenerNotAdded()
            return
        }
        notifyContentAvailable(content)
    }

    private fun notifyContentAvailable(content: AddToListContent) {
        transporter.dispatchToMain {
            listener?.onContentAvailable(content)
        }
    }

    private fun contentListenerNotAdded() {
        AALogger.logError(LISTENER_REGISTRATION_ERROR)
    }
}
