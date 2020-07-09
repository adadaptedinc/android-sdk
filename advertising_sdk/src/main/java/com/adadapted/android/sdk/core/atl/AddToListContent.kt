package com.adadapted.android.sdk.core.atl

interface AddToListContent {
    object Sources {
        const val IN_APP = "in_app"
        const val OUT_OF_APP = "out_of_app"
    }

    fun acknowledge()
    fun itemAcknowledge(item: AddToListItem)
    fun failed(message: String)
    fun itemFailed(item: AddToListItem, message: String)
    fun getSource(): String
    fun getItems(): List<AddToListItem>
    fun hasNoItems(): Boolean
}