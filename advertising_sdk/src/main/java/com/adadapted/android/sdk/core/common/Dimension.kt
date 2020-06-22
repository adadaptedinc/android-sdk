package com.adadapted.android.sdk.core.common

class Dimension {
    object Orientation {
        const val LAND = "land"
        const val PORT = "port"
    }

    var height: Int = 0
    var width: Int = 0

    companion object {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2
    }
}