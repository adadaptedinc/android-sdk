package com.adadapted.android.sdk.ext.json;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.SessionRequestBuilder;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by chrisweeden on 4/6/15.
 */
public class JsonSessionRequestBuilderTest {
    private static final String APPID = "TESTAPPLICATION";
    private static final String UDID = "TESTUDID";
    private static final String BUNDLEID = "TESTBUNDLEID";
    private static final String[] ZONES = new String[]{"10", "11"};
    private static final String DEVICE = "TESTDEVICE";
    private static final String OS = "TESTOS";
    private static final String OSV = "TESTOSV";
    private static final String LOCALE = "locale";
    private static final String TIMEZONE = "timezone";
    private static final int DH = 600;
    private static final int DW = 320;
    private static final int ALLOWRETARGETING = 1;
    private static final String SDKVERSION = "TESTSDKVERSION";

    @Test
    public void testBuildSessionRequestJson_ReturnsJson() throws Exception {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setAppId(APPID);
        deviceInfo.setZones(ZONES);
        deviceInfo.setUdid(UDID);
        deviceInfo.setBundleId(BUNDLEID);
        deviceInfo.setDevice(DEVICE);
        deviceInfo.setOs(OS);
        deviceInfo.setOsv(OSV);
        deviceInfo.setDh(DH);
        deviceInfo.setDw(DW);
        deviceInfo.setSdkVersion(SDKVERSION);

        SessionRequestBuilder builder = new JsonSessionRequestBuilder();
        JSONObject json = builder.buildSessionInitRequest(deviceInfo);

        Assert.assertEquals(APPID, json.getString(JsonFields.APPID));
    }
}