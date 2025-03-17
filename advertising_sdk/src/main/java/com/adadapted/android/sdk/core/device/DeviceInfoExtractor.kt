package com.adadapted.android.sdk.core.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.view.DimensionConverter
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.util.Date
import java.util.TimeZone
import java.util.Locale
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

open class DeviceInfoExtractor(context: Context) {
    private val contextRef = context.applicationContext

    suspend fun extractDeviceInfo(
        appId: String,
        isProd: Boolean,
        customIdentifier: String,
        params: Map<String, String>
    ): DeviceInfo = withContext(Dispatchers.IO) {
        val deferredUdid = async { getUdid(customIdentifier) }

        val metrics = contextRef.resources.displayMetrics
        val scale = metrics.density
        val height = metrics.heightPixels
        val width = metrics.widthPixels
        val density = metrics.densityDpi

        val bundleId = contextRef.packageName
        val bundleVersion = getBundleVersion()
        val carrier = getCarrier()
        val deviceUdid = captureAndroidId()

        DimensionConverter.createInstance(scale, Resources.getSystem().displayMetrics)

        DeviceInfo(
            appId = appId,
            isProd = isProd,
            params = params,
            bundleId = bundleId,
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
            deviceUdid = deviceUdid,
            os = "Android",
            osv = Build.VERSION.SDK_INT.toString(),
            timezone = TimeZone.getDefault().id,
            locale = Locale.getDefault().toString(),
            udid = deferredUdid.await().first,
            isAllowRetargetingEnabled = deferredUdid.await().second,
            bundleVersion = bundleVersion,
            carrier = carrier,
            scale = scale,
            dh = height,
            dw = width,
            density = density.toString(),
            sdkVersion = Config.LIBRARY_VERSION,
            createdAt = Date().time / 1000
        )
    }

    private suspend fun getUdid(customIdentifier: String): Pair<String, Boolean> {
        if (customIdentifier.isNotEmpty()) return Pair(customIdentifier, false)

        return try {
            val info = withContext(Dispatchers.IO) { getAdvertisingIdClientInfo() }
            if (!info?.id.isNullOrEmpty()) {
                Pair(info.id!!, !info.isLimitAdTrackingEnabled)
            } else {
                Pair(captureAndroidId(), false)
            }
        } catch (e: Exception) {
            Pair(captureAndroidId(), false)
        }
    }

    private fun getAdvertisingIdClientInfo(): AdvertisingIdClient.Info? {
        if (isTrackingDisabled()) return null

        return try {
            AdvertisingIdClient.getAdvertisingIdInfo(contextRef)
        } catch (ex: Exception) {
            AALogger.logError("Problem retrieving Google Play Advertising Info: ${ex.message}")
            null
        }
    }

    private fun getBundleVersion(): String = try {
        contextRef.packageManager.getPackageInfo(contextRef.packageName, 0).versionName
            ?: DeviceInfo.UNKNOWN_VALUE
    } catch (_: PackageManager.NameNotFoundException) {
        DeviceInfo.UNKNOWN_VALUE
    }

    private fun getCarrier(): String {
        val manager = contextRef.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return manager.networkOperatorName.ifEmpty { "None" }
    }

    @SuppressLint("HardwareIds")
    private fun captureAndroidId(): String {
        return Settings.Secure.getString(contextRef.contentResolver, Settings.Secure.ANDROID_ID)
            ?: getOrGenerateCustomId()
    }

    private fun getOrGenerateCustomId(): String {
        val sharedPrefs = contextRef.getSharedPreferences(Config.AASDK_PREFS_KEY, Context.MODE_PRIVATE)
        return sharedPrefs.getString(Config.AASDK_PREFS_GENERATED_ID_KEY, "")?.takeIf { it.isNotEmpty() }
            ?: DeviceIdGenerator.generateId().also {
                sharedPrefs.edit { putString(Config.AASDK_PREFS_GENERATED_ID_KEY, it) }
            }
    }

    private fun isTrackingDisabled(): Boolean {
        val sharedPrefs = contextRef.getSharedPreferences(Config.AASDK_PREFS_KEY, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(Config.AASDK_PREFS_TRACKING_DISABLED_KEY, false)
    }
}