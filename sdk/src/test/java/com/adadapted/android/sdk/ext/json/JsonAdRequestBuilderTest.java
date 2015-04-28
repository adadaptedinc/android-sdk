package com.adadapted.android.sdk.ext.json;

import com.adadapted.android.sdk.core.ad.AdRequestBuilder;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.Session;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;

/**
 * Created by chrisweeden on 4/6/15.
 */
public class JsonAdRequestBuilderTest {

    @Test
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

        AdRequestBuilder builder = new JsonAdRequestBuilder();
        JSONObject json = builder.buildAdRequest(deviceInfo, new Session());

        Assert.assertEquals("TESTAPPLICATION", json.getString("app_id"));
        Assert.assertEquals("TESTSESSION", json.getString("session_id"));
        Assert.assertEquals("TESTUDID", json.getString("udid"));
        Assert.assertEquals(2, json.getJSONArray("zones").length());
        Assert.assertNotNull(json.getLong("datetime"));
    }
}