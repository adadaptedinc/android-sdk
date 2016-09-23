package com.adadapted.android.sdk.core.device.model;

import android.content.Context;
import android.content.pm.PackageManager;
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
    private static final String LOGTAG = DeviceInfo.class.getName();

    private static final String UNKNOWN_VALUE = "Unknown";

    private String appId;
    private boolean isProd;
    private float scale;
    private String bundleId;
    private String bundleVersion;
    private String udid;
    private String device;
    private String deviceUdid;
    private String os;
    private String osv;
    private String locale;
    private String timezone;
    private String carrier;
    private int dw;
    private int dh;
    private ScreenDensity density;
    private boolean allowRetargeting;
    private String sdkVersion;

    public DeviceInfo() {}

    public static DeviceInfo captureDeviceInfo(final Context context,
                                               final String appId,
                                               final boolean isProd,
                                               final String sdkVersion) {
        final DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setAppId(appId);
        deviceInfo.setIsProd(isProd);
        deviceInfo.setScale(context.getResources().getDisplayMetrics().density);

        deviceInfo.setUdid(deviceInfo.captureAdvertisingId(context));

        deviceInfo.setBundleId(context.getPackageName());

        try {
            final String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            deviceInfo.setBundleVersion(version);
        }
        catch(PackageManager.NameNotFoundException ex) {
            deviceInfo.setBundleVersion(UNKNOWN_VALUE);
        }

        deviceInfo.setDevice(Build.MANUFACTURER + " " + Build.MODEL);
        deviceInfo.setDeviceUdid(deviceInfo.captureAndroidId(context));
        deviceInfo.setOs("Android");
        deviceInfo.setOsv(Integer.toString(Build.VERSION.SDK_INT));

        deviceInfo.setTimezone(TimeZone.getDefault().getID());
        deviceInfo.setLocale(Locale.getDefault().toString());

        final TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        final String carrier = manager.getNetworkOperatorName().length() > 0 ? manager.getNetworkOperatorName() : "None";
        deviceInfo.setCarrier(carrier);

        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        deviceInfo.setDh(metrics.heightPixels);
        deviceInfo.setDw(metrics.widthPixels);

        deviceInfo.setDensity(deviceInfo.determineScreenDensity(context));

        deviceInfo.setAllowRetargeting(deviceInfo.captureRetargetingEnabled(context));

        deviceInfo.setSdkVersion(sdkVersion);

        return deviceInfo;
    }

    private String captureAdvertisingId(final Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        }
        catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException ex) {
            Log.w(LOGTAG, "Problem retrieving Google Play Advertiser Info");
        }

        return captureAndroidId(context);
    }

    private boolean captureRetargetingEnabled(Context context) {
        try {
            return !AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled();
        }
        catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException ex) {
            Log.w(LOGTAG, "Problem retrieving Google Play Advertiser Info");
        }

        return true;
    }

    private String captureAndroidId(final Context context) {
        final String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return androidId == null ? "" : androidId;
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

    public void setAppId(final String appId) {
        this.appId = appId;
    }

    public boolean isProd() {
        return isProd;
    }

    public void setIsProd(final boolean isProd) {
        this.isProd = isProd;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(final float scale) {
        this.scale = scale;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(final String bundleId) {
        this.bundleId = (bundleId == null) ? UNKNOWN_VALUE : bundleId;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = (bundleVersion == null) ? UNKNOWN_VALUE : bundleVersion;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(final String udid) {
        this.udid = (udid == null) ? UNKNOWN_VALUE : udid;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(final String device) {
        this.device = (device == null) ? UNKNOWN_VALUE : device;
    }

    public String getDeviceUdid() {
        return deviceUdid;
    }

    public void setDeviceUdid(final String deviceUdid) {
        this.deviceUdid = (deviceUdid == null) ? UNKNOWN_VALUE : deviceUdid;
    }

    public String getOs() {
        return os;
    }

    public void setOs(final String os) {
        this.os = (os == null) ? UNKNOWN_VALUE : os;
    }

    public String getOsv() {
        return osv;
    }

    public void setOsv(final String osv) {
        this.osv = (osv == null) ? UNKNOWN_VALUE : osv;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = (locale == null) ? UNKNOWN_VALUE : locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = (timezone == null) ? UNKNOWN_VALUE : timezone;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(final String carrier) {
        this.carrier = (carrier == null) ? UNKNOWN_VALUE : carrier;
    }

    public int getDw() {
        return dw;
    }

    public void setDw(final int dw) {
        this.dw = dw;
    }

    public int getDh() {
        return dh;
    }

    public void setDh(final int dh) {
        this.dh = dh;
    }

    public ScreenDensity getDensity() {
        return density;
    }

    public void setDensity(final ScreenDensity density) {
        this.density = density;
    }

    public boolean allowRetargetingEnabled() {
        return allowRetargeting;
    }

    public void setAllowRetargeting(boolean allowRetargeting) {
        this.allowRetargeting = allowRetargeting;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(final String sdkVersion) {
        this.sdkVersion = (sdkVersion == null) ? UNKNOWN_VALUE : sdkVersion;
    }
}
