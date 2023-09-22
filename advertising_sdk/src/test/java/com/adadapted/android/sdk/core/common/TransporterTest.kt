package com.adadapted.android.sdk.core.common

import org.junit.Test

class TransporterTest {
    @Test
    fun testTransporterBackgroundFunc() {
        val transporter = Transporter()
        transporter.dispatchToBackground { backgroundFunc() }
    }

    private fun backgroundFunc() {
        assert(true)
    }
}