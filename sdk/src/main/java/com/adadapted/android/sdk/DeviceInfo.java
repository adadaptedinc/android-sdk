package com.adadapted.android.sdk;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by chrisweeden on 3/23/15.
 */
class DeviceInfo {
    private static final String TAG = DeviceInfo.class.getName();

    private String appId;
    private String[] zones;
    private String bundleId;
    private String udid;
    private String device;
    private String os;
    private String osv;
    private String locale;
    private String timezone;
    private int dw;
    private int dh;

    private DeviceInfo() {}

    static DeviceInfo captureDeviceInfo(Context context, String appId, String[] zones) {
        DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setAppId(appId);
        deviceInfo.setZones(zones);

        deviceInfo.setUdid(deviceInfo.getAdvertisingId(context));

        deviceInfo.setBundleId(context.getPackageName());
        deviceInfo.setDevice(Build.MANUFACTURER + " " + Build.MODEL);
        deviceInfo.setOs("Android");
        deviceInfo.setOsv(Integer.toString(Build.VERSION.SDK_INT));

        deviceInfo.setTimezone(TimeZone.getDefault().getID());
        deviceInfo.setLocale(Locale.getDefault().toString());

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        deviceInfo.setDh(metrics.heightPixels);
        deviceInfo.setDw(metrics.widthPixels);

        return deviceInfo;
    }

    private String getAdvertisingId(Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        }
        catch (IOException ex) {
            Log.w(TAG, "Problem retrieving Google Play AdvertiserId", ex);
            return getAndroidId(context);
        }
        catch (GooglePlayServicesNotAvailableException ex) {
            Log.w(TAG, "Problem retrieving Google Play AdvertiserId", ex);
            return getAndroidId(context);
        }
        catch(GooglePlayServicesRepairableException ex) {
            Log.w(TAG, "Problem retrieving Google Play AdvertiserId", ex);
            return getAndroidId(context);
        }
    }

    private String getAndroidId(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String[] getZones() {
        return zones;
    }

    public void setZones(String[] zones) {
        this.zones = zones;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsv() {
        return osv;
    }

    public void setOsv(String osv) {
        this.osv = osv;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getDw() {
        return dw;
    }

    public void setDw(int dw) {
        this.dw = dw;
    }

    public int getDh() {
        return dh;
    }

    public void setDh(int dh) {
        this.dh = dh;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "appId='" + appId + '\'' +
                ", zones=" + Arrays.toString(zones) +
                ", bundleId='" + bundleId + '\'' +
                ", udid='" + udid + '\'' +
                ", device='" + device + '\'' +
                ", os='" + os + '\'' +
                ", osv='" + osv + '\'' +
                ", locale='" + locale + '\'' +
                ", timezone='" + timezone + '\'' +
                ", dw=" + dw +
                ", dh=" + dh +
                '}';
    }
}
