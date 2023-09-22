package com.adadapted.android.sdk.core.concurrency

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class Transporter : TransporterCoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}
