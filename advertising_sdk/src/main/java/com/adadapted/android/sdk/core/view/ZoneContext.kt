package com.adadapted.android.sdk.core.view

data class ZoneContext(val zoneId: String = "", val contextId: String = "") {
    override fun equals(other: Any?): Boolean {
        return other is ZoneContext && this.zoneId == other.zoneId
    }

    override fun hashCode(): Int {
        return zoneId.hashCode()
    }
}