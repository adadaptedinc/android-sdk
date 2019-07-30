package com.adadapted.android.sdk.core.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeviceInfoClient {
    private static final String LOGTAG = DeviceInfoClient.class.getName();

    public interface Callback {
        void onDeviceInfoCollected(DeviceInfo deviceInfo);
    }

    private static DeviceInfoClient instance;

    private static synchronized DeviceInfoClient getInstance() {
        if(instance == null) {
            instance = new DeviceInfoClient();
        }

        return instance;
    }

    public static synchronized void collectDeviceInfo(
        final Context context,
        final String appId,
        final boolean isProd,
        final Map<String, String> params,
        final Callback callback
    ) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performCollectInfo(context.getApplicationContext(), appId, isProd, params, callback);
            }
        });
    }

    public static synchronized void getDeviceInfo(final Callback callback) {
        ThreadPoolInteractorExecuter.getInstance().executeInBackground(new Runnable() {
            @Override
            public void run() {
                getInstance().performGetInfo(callback);
            }
        });
    }

    private DeviceInfo deviceInfo;
    private final Lock lock = new ReentrantLock();

    private final Set<Callback> callbacks;

    private DeviceInfoClient() {
        callbacks = new HashSet<>();
    }

    private void performGetInfo(final Callback callback) {
        lock.lock();
        try {
            if(deviceInfo != null) {
                callback.onDeviceInfoCollected(deviceInfo);
            }
            else {
                callbacks.add(callback);
            }
        }
        finally {
            lock.unlock();
        }
    }

    private void performCollectInfo(final Context context,
                                    final String appId,
                                    final boolean isProd,
                                    final Map<String, String> params,
                                    final Callback callback) {
        final DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setAppId(appId);
        deviceInfo.setProd(isProd);
        deviceInfo.setParams(params);

        try {
            Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");

            final AdvertisingIdClient.Info info = getAdvertisingIdClientInfo(context);
            if (info != null) {
                deviceInfo.setUdid(info.getId());
                deviceInfo.setAllowRetargeting(!info.isLimitAdTrackingEnabled());
            } else {
                deviceInfo.setUdid(captureAndroidId(context));
                deviceInfo.setAllowRetargeting(false);
            }
        }
        catch(ClassNotFoundException e) {
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
            deviceInfo.setDensity(metrics.densityDpi);
        }

        lock.lock();
        try {
            this.deviceInfo = deviceInfo;
        }
        finally {
            lock.unlock();
        }

        if(callback != null) {
            notifyCallback(callback);
        }
        else {
            Log.w(LOGTAG, "Collect Device Info callback is NULL");
        }
    }

    private void notifyCallback(final Callback callback) {
        lock.lock();
        try {
            callback.onDeviceInfoCollected(deviceInfo);

            final Set<Callback> current = new HashSet<>(callbacks);
            for(final Callback c : current) {
                c.onDeviceInfoCollected(deviceInfo);
                callbacks.remove(c);
            }
        }
        finally {
            lock.unlock();
        }
    }

    private AdvertisingIdClient.Info getAdvertisingIdClientInfo(final Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context);
        }
        catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException ex) {
            Log.w(LOGTAG, "Problem retrieving Google Play Advertiser Info");

            AppEventClient.trackError("GAID_UNAVAILABLE", ex.getMessage());
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
}
