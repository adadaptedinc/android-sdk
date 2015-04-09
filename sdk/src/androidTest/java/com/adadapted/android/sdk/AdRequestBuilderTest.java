package com.adadapted.android.sdk;

import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 4/6/15.
 */
public class AdRequestBuilderTest extends InstrumentationTestCase {

    public void testBuildAdRequestJson_ReturnsJson() throws Exception {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setAppId("TESTAPPLICATION");
        deviceInfo.setZones(new String[]{"10", "11"});
        deviceInfo.setUdid("TESTUDID");
        deviceInfo.setBundleId("com.adadapted.android.sdk");
        deviceInfo.setDevice("Test Device");
        deviceInfo.setOs("Android");
        deviceInfo.setOsv("22");
        deviceInfo.setDh(600);
        deviceInfo.setDw(320);

        Session session = new Session();
        session.setSessionId("TESTSESSION");
        session.setActiveCampaigns(true);
        session.setExpiresAt(new Date());

        AdRequestBuilder builder = new AdRequestBuilder();
        JSONObject json = builder.buildAdRequestJson(deviceInfo, new Session());

        Assert.assertEquals("TESTAPPLICATION", json.getString("app_id"));
        Assert.assertEquals("TESTSESSION", json.getString("session_id"));
        Assert.assertEquals("TESTUDID", json.getString("udid"));
        Assert.assertEquals(2, json.getJSONArray("zones").length());
        Assert.assertNotNull(json.getLong("datetime"));
    }
}