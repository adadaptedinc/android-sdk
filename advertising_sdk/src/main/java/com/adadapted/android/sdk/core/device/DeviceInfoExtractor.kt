package com.adadapted.android.sdk.core.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.adadapted.android.sdk.config.Config
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import java.io.IOException
import java.util.TimeZone
import java.util.Locale

class DeviceInfoExtractor: InfoExtractor {
    override fun extractDeviceInfo(context: Context, appId: String, isProd: Boolean, params: Map<String, String>) : DeviceInfo {
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

        return deviceInfo
    }

    private fun captureAndroidId(context: Context): String {
        @SuppressLint("HardwareIds") val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return androidId ?: ""
    }

    private fun getAdvertisingIdClientInfo(context: Context): AdvertisingIdClient.Info? {
        if (isTrackingDisabled(context)) {
            return null
        }

        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context.applicationContext)
        } catch (ex: GooglePlayServicesNotAvailableException) {
            trackGooglePlayAdError(ex)
        } catch (ex: GooglePlayServicesRepairableException) {
            trackGooglePlayAdError(ex)
        } catch (ex: IOException) {
            trackGooglePlayAdError(ex)
        }
        return null
    }

    private fun isTrackingDisabled(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(Config.AASDK_PREFS_KEY, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(Config.AASDK_PREFS_TRACKING_DISABLED_KEY, false)
    }

    private fun trackGooglePlayAdError(ex: Exception) {
        Log.w(LOGTAG, GooglePlayAdError)
    }

    companion object {
        private val LOGTAG = DeviceInfoExtractor::class.java.name
        private const val GooglePlayAdError = "Problem retrieving Google Play Advertiser Info"
        private const val AdvertisingIdClientName = "com.google.android.gms.ads.identifier.AdvertisingIdClient"
        private const val NetworkOperatorDefault = "None"
    }
}