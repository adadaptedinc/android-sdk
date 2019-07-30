package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.junit.Test;

import static org.junit.Assert.*;

public class SessionTest {
    @Test
    public void emptySession() {
        final DeviceInfo deviceInfo = DeviceInfo.empty();
        final Session session = Session.emptySession(deviceInfo);

        assertEquals("", session.getId());
    }
}