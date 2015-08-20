package com.adadapted.android.sdk.ext.json;

import com.adadapted.android.sdk.core.ad.AdRequestBuilder;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;

/**
 * Created by chrisweeden on 4/6/15.
 */
public class JsonAdRequestBuilderTest {
    private static final String APPID = "TESTAPPLICATION";
    private static final String UDID = "TESTUDID";
    private static final String BUNDLEID = "com.adadapted.android.sdk";
    private static final String DEVICE = "Test Device";
    private static final String OS = "Android";
    private static final String OSV = "22";
    private static final int DH = 600;
    private static final int DW = 320;
    private static final String SDKVERSION = "TESTSDKVERSION";
    private static final String SESSIONID = "TESTSESSION";

    @Test
    public void testBuildAdRequestJson_ReturnsJson() throws Exception {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setAppId(APPID);
        deviceInfo.setUdid(UDID);
        deviceInfo.setBundleId(BUNDLEID);
        deviceInfo.setDevice(DEVICE);
        deviceInfo.setOs(OS);
        deviceInfo.setOsv(OSV);
        deviceInfo.setDh(DH);
        deviceInfo.setDw(DW);
        deviceInfo.setSdkVersion(SDKVERSION);

        Session session = new Session();
        session.setSessionId(SESSIONID);
        session.setActiveCampaigns(true);
        session.setExpiresAt(new Date());

        AdRequestBuilder builder = new JsonAdRequestBuilder();
        JSONObject json = builder.buildAdRequest(deviceInfo, session);

        Assert.assertEquals(APPID, json.getString(JsonFields.APPID));
        Assert.assertEquals(SESSIONID, json.getString(JsonFields.SESSIONID));
        Assert.assertEquals(UDID, json.getString(JsonFields.UDID));
        Assert.assertEquals(2, json.getJSONArray(JsonFields.ZONES).length());
        Assert.assertNotNull(json.getLong(JsonFields.DATETIME));
    }
}