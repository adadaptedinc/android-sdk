package com.adadapted.android.sdk.core.device

object DeviceIdGenerator {
    private val IdCharacters: List<Char> = ('a'..'z') + ('0'..'9')

    fun generateId(): String {
        return List(32) { IdCharacters.random() }.joinToString("")
    }
}