package com.adadapted.android.sdk.core.intercept

import java.util.Date

class InterceptEvent internal constructor(val searchId: String = "",
                                          val event: String = "",
                                          val userInput: String = "",
                                          val termId: String = "",
                                          val term: String = "") {

    val createdAt: Date = Date()

    fun supersedes(e: InterceptEvent): Boolean {
        return event == e.event && termId == e.termId && userInput.contains(e.userInput)
    }

    override fun toString(): String {
        return "InterceptEvent{" +
                "searchId='" + searchId + '\'' +
                ", createdAt=" + createdAt +
                ", event='" + event + '\'' +
                ", userInput='" + userInput + '\'' +
                ", termId='" + termId + '\'' +
                ", term='" + term + '\'' +
                '}'
    }

    companion object {
        const val MATCHED = "matched"
        const val NOT_MATCHED = "not_matched"
        const val PRESENTED = "presented"
        const val SELECTED = "selected"
    }
}