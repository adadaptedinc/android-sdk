package com.adadapted.android.sdk.core.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.event.AppEventClient
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import java.io.IOException
import java.lang.Exception
import java.util.TimeZone
import java.util.Locale
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class DeviceInfoClient private constructor(
        context: Context,
        appId: String,
        isProd: Boolean,
        params: Map<String, String>,
        private val advertisingIdClientWrapper: AdvertisingIdClientWrapper,
        private val transporter: TransporterCoroutineScope) {

    interface Callback {
        fun onDeviceInfoCollected(deviceInfo: DeviceInfo)
    }

    private lateinit var deviceInfo: DeviceInfo
    private val lock: Lock = ReentrantLock()
    private val callbacks: MutableSet<Callback>

    private fun performGetInfo(callback: Callback) {
        lock.lock()
        try {
            if (this::deviceInfo.isInitialized) {
                callback.onDeviceInfoCollected(deviceInfo)
            } else {
                callbacks.add(callback)
            }
        } finally {
            lock.unlock()
        }
    }

    private fun performCollectInfo(context: Context,
                                   appId: String,
                                   isProd: Boolean,
                                   params: Map<String, String>) {
        val deviceInfo = DeviceInfo()
        deviceInfo.appId = appId
        deviceInfo.isProd = isProd
        deviceInfo.params = params
        deviceInfo.bundleId = context.packageName
        deviceInfo.device = Build.MANUFACTURER + " " + Build.MODEL
        deviceInfo.deviceUdid = captureAndroidId(context)
        deviceInfo.osv = Build.VERSION.SDK_INT.toString()
        deviceInfo.timezone = TimeZone.getDefault().id
        deviceInfo.locale = Locale.getDefault().toString()

        try {
            Class.forName(AdvertisingIdClientName)
            val info = getAdvertisingIdClientInfo(context)
            if (info != null) {
                deviceInfo.udid = info.id
                deviceInfo.setAllowRetargeting(!info.isLimitAdTrackingEnabled)
            } else {
                deviceInfo.udid = captureAndroidId(context)
                deviceInfo.setAllowRetargeting(false)
            }
        } catch (e: ClassNotFoundException) {
            deviceInfo.udid = captureAndroidId(context)
            deviceInfo.setAllowRetargeting(false)
        }

        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = if (packageInfo != null) packageInfo.versionName else DeviceInfo.UNKNOWN_VALUE
            deviceInfo.bundleVersion = version
        } catch (ex: PackageManager.NameNotFoundException) {
            deviceInfo.bundleVersion = DeviceInfo.UNKNOWN_VALUE
        }

        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val carrier = if (manager.networkOperatorName.isNotEmpty()) manager.networkOperatorName else NetworkOperatorDefault
        deviceInfo.carrier = carrier

        val metrics = context.resources.displayMetrics
        if (metrics != null) {
            deviceInfo.scale = metrics.density
            deviceInfo.dh = metrics.heightPixels
            deviceInfo.dw = metrics.widthPixels
            deviceInfo.density = metrics.densityDpi
        }

        lock.lock()
        try {
            this.deviceInfo = deviceInfo
        } finally {
            lock.unlock()
        }
        notifyCallbacks()
    }

    private fun notifyCallbacks() {
        lock.lock()
        try {
            val currentCallbacks: Set<Callback> = HashSet(callbacks)
            for (caller in currentCallbacks) {
                caller.onDeviceInfoCollected(deviceInfo)
                callbacks.remove(caller)
            }
        } finally {
            lock.unlock()
        }
    }

    private fun getAdvertisingIdClientInfo(context: Context): AdvertisingIdClient.Info? {
        try {
            return advertisingIdClientWrapper.requestAdvertisingIdInfo(context)
        } catch (ex: GooglePlayServicesNotAvailableException) {
            trackGooglePlayAdError(ex)
        } catch (ex: GooglePlayServicesRepairableException) {
            trackGooglePlayAdError(ex)
        } catch (ex: IOException) {
            trackGooglePlayAdError(ex)
        }
        return null
    }

    private fun captureAndroidId(context: Context): String {
        @SuppressLint("HardwareIds") val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID)
        return androidId ?: ""
    }

    private fun trackGooglePlayAdError(ex: Exception) {
        Log.w(LOGTAG, GooglePlayAdError)
        ex.message?.let { AppEventClient.getInstance().trackError(EventStrings.GAID_UNAVAILABLE, it) }
    }

    @Synchronized
    fun getDeviceInfo(callback: Callback) {
        transporter.dispatchToBackground {
            performGetInfo(callback)
        }
    }

    companion object {
        private val LOGTAG = DeviceInfoClient::class.java.name
        private const val AdvertisingIdClientName = "com.google.android.gms.ads.identifier.AdvertisingIdClient"
        private const val GooglePlayAdError = "Problem retrieving Google Play Advertiser Info"
        private const val NetworkOperatorDefault = "None"
        private lateinit var instance: DeviceInfoClient

        fun createInstance(context: Context,
                           appId: String,
                           isProd: Boolean,
                           params: Map<String, String>,
                           advertisingIdClientWrapper: AdvertisingIdClientWrapper = AdvertisingIdClientWrapper(),
                           transporter: TransporterCoroutineScope) {
            instance = DeviceInfoClient(context, appId, isProd, params, advertisingIdClientWrapper, transporter)
        }

        fun getInstance(): DeviceInfoClient {
            return instance
        }
    }

    init {
        callbacks = HashSet()
        transporter.dispatchToBackground {
            performCollectInfo(context.applicationContext, appId, isProd, params)
        }
    }
}