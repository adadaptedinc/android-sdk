package com.adadapted.android.sdk.core.common

class Dimension(var height: Int = 0, var width: Int = 0) {
    object Orientation {
        const val LAND = "land"
        const val PORT = "port"
    }

    companion object {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2
    }
}