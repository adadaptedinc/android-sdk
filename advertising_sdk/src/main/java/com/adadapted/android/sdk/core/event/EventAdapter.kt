package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.session.Session

interface EventAdapter {
    suspend fun publishAdEvents(session: Session, adEvents: Set<AdEvent>)
    suspend fun publishSdkEvents(session: Session, events: Set<SdkEvent>)
    suspend fun publishSdkErrors(session: Session, errors: Set<SdkError>)
}