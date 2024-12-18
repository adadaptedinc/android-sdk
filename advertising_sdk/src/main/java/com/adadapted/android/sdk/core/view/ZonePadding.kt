package com.adadapted.android.sdk.core.view

data class ZonePadding(
    val top: Int,
    val bottom: Int,
    val start: Int,
    val end: Int
) {
    constructor(value: Int) : this(
        top = value,
        bottom = value,
        start = value,
        end = value
    )
}

fun ZonePadding.isNotZero(): Boolean {
    return top > 0 || bottom > 0 || start > 0 || end > 0
}
