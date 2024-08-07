package com.adadapted.android.sdk.core.keyword

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class InterceptEvent(
    @SerialName("search_id")
    val searchId: String = "",
    @SerialName("event_type")
    val event: String = "",
    @SerialName("user_input")
    val userInput: String = "",
    @SerialName("term_id")
    val termId: String = "",
    val term: String = "",
    @SerialName("created_at")
    val createdAt: Long = Date().time / 1000
) {

    fun supersedes(e: InterceptEvent): Boolean {
        return event == e.event && termId == e.termId && userInput.contains(e.userInput)
    }

    companion object {
        const val MATCHED = "matched"
        const val NOT_MATCHED = "not_matched"
        const val PRESENTED = "presented"
        const val SELECTED = "selected"
    }
}