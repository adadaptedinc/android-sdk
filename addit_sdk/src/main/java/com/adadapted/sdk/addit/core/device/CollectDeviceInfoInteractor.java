package com.adadapted.sdk.addit.core.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.adadapted.sdk.addit.core.common.Interactor;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import static com.google.ads.AdRequest.LOGTAG;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class CollectDeviceInfoInteractor implements Interactor {
    private final CollectDeviceInfoCommand command;
    private final Callback callback;

    public CollectDeviceInfoInteractor(final CollectDeviceInfoCommand command,
                                       final Callback callback) {
        this.command = command;
        this.callback = callback;
    }

    @Override
    public void execute() {
        final DeviceInfo deviceInfo = new DeviceInfo();
        final Context context = command.getContext();

        deviceInfo.setAppId(command.getAppId());
        deviceInfo.setProd(command.isProd());

        final AdvertisingIdClient.Info info = getAdvertisingIdClientInfo(context);
        if (info != null) {
            deviceInfo.setUdid(info.getId());
            deviceInfo.setAllowRetargeting(info.isLimitAdTrackingEnabled());
        }
        else {
            deviceInfo.setUdid(captureAndroidId(context));
            deviceInfo.setAllowRetargeting(false);
        }

        deviceInfo.setBundleId(context.getPackageName());

        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            final String version = packageInfo != null ? packageInfo.versionName : DeviceInfo.UNKNOWN_VALUE;
            deviceInfo.setBundleVersion(version);
        }
        catch(PackageManager.NameNotFoundException ex) {
            deviceInfo.setBundleVersion(DeviceInfo.UNKNOWN_VALUE);
        }

        deviceInfo.setDevice(Build.MANUFACTURER + " " + Build.MODEL);
        deviceInfo.setDeviceUdid(captureAndroidId(context));
        deviceInfo.setOs("Android");
        deviceInfo.setOsv(Integer.toString(Build.VERSION.SDK_INT));

        deviceInfo.setTimezone(TimeZone.getDefault().getID());
        deviceInfo.setLocale(Locale.getDefault().toString());

        final TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        final String carrier = (manager != null && manager.getNetworkOperatorName().length() > 0) ? manager.getNetworkOperatorName() : "None";
        deviceInfo.setCarrier(carrier);

        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (metrics != null) {
            deviceInfo.setScale(metrics.density);
            deviceInfo.setDh(metrics.heightPixels);
            deviceInfo.setDw(metrics.widthPixels);
            deviceInfo.setDensity(determineScreenDensity(metrics));
        }

        deviceInfo.setSdkVersion(command.getSdkVersion());

        callback.onDeviceInfoCollected(deviceInfo);
    }

    private AdvertisingIdClient.Info getAdvertisingIdClientInfo(final Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context);
        }
        catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException ex) {
            Log.w(LOGTAG, "Problem retrieving Google Play Advertiser Info");
        }

        return null;
    }

    private String captureAndroidId(final Context context) {
        @SuppressLint("HardwareIds")
        final String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return androidId == null ? "" : androidId;
    }

    private ScreenDensity determineScreenDensity(final DisplayMetrics metrics) {
        int density = metrics.densityDpi;
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

    public interface Callback {
        void onDeviceInfoCollected(DeviceInfo deviceInfo);
    }
}
