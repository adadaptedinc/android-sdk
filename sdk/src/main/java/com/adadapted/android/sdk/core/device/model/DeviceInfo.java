package com.adadapted.android.sdk.core.device.model;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.ImageAdType;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class DeviceInfo {
    private static final String TAG = DeviceInfo.class.getName();

    private static final String UNKNOWN_VALUE = "Unknown";

    private String appId;
    private String[] zones;
    private String bundleId;
    private String udid;
    private String device;
    private String os;
    private String osv;
    private String locale;
    private String timezone;
    private String carrier;
    private int dw;
    private int dh;
    private ScreenDensity density;
    private String sdkVersion;

    public DeviceInfo() {}

    public static DeviceInfo captureDeviceInfo(Context context, String appId, String[] zones, String sdkVersion) {
        DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setAppId(appId);
        deviceInfo.setZones(zones);

        deviceInfo.setUdid(deviceInfo.captureAdvertisingId(context));

        deviceInfo.setBundleId(context.getPackageName());
        deviceInfo.setDevice(Build.MANUFACTURER + " " + Build.MODEL);
        deviceInfo.setOs("Android");
        deviceInfo.setOsv(Integer.toString(Build.VERSION.SDK_INT));

        deviceInfo.setTimezone(TimeZone.getDefault().getID());
        deviceInfo.setLocale(Locale.getDefault().toString());

        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrier = manager.getNetworkOperatorName().length() > 0 ? manager.getNetworkOperatorName() : "None";
        deviceInfo.setCarrier(carrier);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        deviceInfo.setDh(metrics.heightPixels);
        deviceInfo.setDw(metrics.widthPixels);

        deviceInfo.setDensity(deviceInfo.determineScreenDensity(context));

        deviceInfo.setSdkVersion(sdkVersion);

        return deviceInfo;
    }

    private String captureAdvertisingId(final Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        }
        catch (GooglePlayServicesNotAvailableException ex) {
            Log.w(TAG, "Problem retrieving Google Play AdvertiserId", ex);
        }
        catch (GooglePlayServicesRepairableException ex) {
            Log.w(TAG, "Problem retrieving Google Play AdvertiserId", ex);
        }
        catch (IOException ex) {
            Log.w(TAG, "Problem retrieving Google Play AdvertiserId", ex);
        }

        return captureAndroidId(context);
    }

    private String captureAndroidId(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private ScreenDensity determineScreenDensity(final Context context) {
        int density = context.getResources().getDisplayMetrics().densityDpi;
        switch (density)
        {
            case DisplayMetrics.DENSITY_MEDIUM:
                return ScreenDensity.MDPI;
            case DisplayMetrics.DENSITY_HIGH:
                return ScreenDensity.HDPI;
            case DisplayMetrics.DENSITY_LOW:
                return ScreenDensity.LDPI;
            case DisplayMetrics.DENSITY_XHIGH:
                return ScreenDensity.XHDPI;
            case DisplayMetrics.DENSITY_TV:
                return ScreenDensity.TV;
            case DisplayMetrics.DENSITY_XXHIGH:
                return ScreenDensity.XXHDPI;
            case DisplayMetrics.DENSITY_XXXHIGH:
                return ScreenDensity.XXXHDPI;
            default:
                return ScreenDensity.UNKNOWN;
        }
    }

    public String chooseImageSize() {
        if(density == ScreenDensity.MDPI || density == ScreenDensity.HDPI) {
            return ImageAdType.STANDARD_IMAGE;
        }

        return ImageAdType.RETINA_IMAGE;
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
        this.bundleId = (bundleId == null) ? UNKNOWN_VALUE : bundleId;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = (udid == null) ? UNKNOWN_VALUE : udid;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = (device == null) ? UNKNOWN_VALUE : device;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = (os == null) ? UNKNOWN_VALUE : os;
    }

    public String getOsv() {
        return osv;
    }

    public void setOsv(String osv) {
        this.osv = (osv == null) ? UNKNOWN_VALUE : osv;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = (locale == null) ? UNKNOWN_VALUE : locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = (timezone == null) ? UNKNOWN_VALUE : timezone;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = (carrier == null) ? UNKNOWN_VALUE : carrier;
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

    public ScreenDensity getDensity() {
        return density;
    }

    public void setDensity(ScreenDensity density) {
        this.density = density;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = (sdkVersion == null) ? UNKNOWN_VALUE : sdkVersion;
    }
}
