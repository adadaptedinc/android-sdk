package com.adadapted.sdk.addit.core.device;

/**
 * Created by chrisweeden on 9/26/16.
 */

public class DeviceInfo {
    static final String UNKNOWN_VALUE = "Unknown";

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isProd() {
        return isProd;
    }

    public void setProd(boolean prod) {
        isProd = prod;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
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

    public String getDeviceUdid() {
        return deviceUdid;
    }

    public void setDeviceUdid(String deviceUdid) {
        this.deviceUdid = deviceUdid;
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

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
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

    public boolean isAllowRetargetingEnabled() {
        return allowRetargeting;
    }

    public void setAllowRetargeting(boolean allowRetargeting) {
        this.allowRetargeting = allowRetargeting;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
}
