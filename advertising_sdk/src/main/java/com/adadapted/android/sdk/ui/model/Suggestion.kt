package com.adadapted.android.sdk.ui.model

import android.os.Parcel
import android.os.Parcelable
import com.adadapted.android.sdk.core.intercept.Term
import com.adadapted.android.sdk.ui.adapter.SuggestionTracker.suggestionPresented
import com.adadapted.android.sdk.ui.adapter.SuggestionTracker.suggestionSelected

class Suggestion : Parcelable {
    val searchId: String
    val termId: String
    val name: String
    val icon: String
    val tagLine: String
    var presented: Boolean
    var selected: Boolean

    constructor(searchId: String, term: Term) {
        this.searchId = searchId
        termId = term.termId
        name = term.replacement
        icon = term.icon
        tagLine = term.tagLine
        presented = false
        selected = false
    }

    private constructor(parcel: Parcel) {
        searchId = parcel.readString()
        termId = parcel.readString()
        name = parcel.readString()
        icon = parcel.readString()
        tagLine = parcel.readString()
        presented = parcel.readByte().toInt() != 0
        selected = parcel.readByte().toInt() != 0
    }

    fun presented() {
        if (!presented) {
            presented = true
            suggestionPresented(searchId, termId, name)
        }
    }

    fun selected() {
        if (!selected) {
            selected = true
            suggestionSelected(searchId, termId, name)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(searchId)
        parcel.writeString(termId)
        parcel.writeString(name)
        parcel.writeString(icon)
        parcel.writeString(tagLine)
        parcel.writeByte((if (presented) 1 else 0).toByte())
        parcel.writeByte((if (selected) 1 else 0).toByte())
    }

    companion object CREATOR : Parcelable.Creator<Suggestion> {
        override fun createFromParcel(parcel: Parcel): Suggestion {
            return Suggestion(parcel)
        }

        override fun newArray(size: Int): Array<Suggestion?> {
            return arrayOfNulls(size)
        }
    }
}