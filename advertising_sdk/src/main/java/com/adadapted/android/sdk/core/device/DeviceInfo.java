package com.adadapted.android.sdk.core.device;

import com.adadapted.android.sdk.core.ad.model.ImageAdType;

import java.util.Map;

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
    private Map<String, String> params;

    public String getAppId() {
        return appId;
    }

    void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isProd() {
        return isProd;
    }

    void setProd(boolean prod) {
        isProd = prod;
    }

    public float getScale() {
        return scale;
    }

    void setScale(float scale) {
        this.scale = scale;
    }

    public String getBundleId() {
        return bundleId;
    }

    void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

    public String getUdid() {
        return udid;
    }

    void setUdid(String udid) {
        this.udid = udid;
    }

    public String getDevice() {
        return device;
    }

    void setDevice(String device) {
        this.device = device;
    }

    public String getDeviceUdid() {
        return deviceUdid;
    }

    void setDeviceUdid(String deviceUdid) {
        this.deviceUdid = deviceUdid;
    }

    public String getOs() {
        return os;
    }

    void setOs(String os) {
        this.os = os;
    }

    public String getOsv() {
        return osv;
    }

    void setOsv(String osv) {
        this.osv = osv;
    }

    public String getLocale() {
        return locale;
    }

    void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCarrier() {
        return carrier;
    }

    void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public int getDw() {
        return dw;
    }

    void setDw(int dw) {
        this.dw = dw;
    }

    public int getDh() {
        return dh;
    }

    void setDh(int dh) {
        this.dh = dh;
    }

    public ScreenDensity getDensity() {
        return density;
    }

    void setDensity(ScreenDensity density) {
        this.density = density;
    }

    public boolean isAllowRetargetingEnabled() {
        return allowRetargeting;
    }

    void setAllowRetargeting(boolean allowRetargeting) {
        this.allowRetargeting = allowRetargeting;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String chooseImageSize() {
        if(density == ScreenDensity.MDPI || density == ScreenDensity.HDPI) {
            return ImageAdType.STANDARD_IMAGE;
        }

        return ImageAdType.RETINA_IMAGE;
    }
}
