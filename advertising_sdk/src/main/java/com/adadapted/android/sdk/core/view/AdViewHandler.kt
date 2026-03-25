package com.adadapted.android.sdk.core.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.network.HttpErrorTracker

class AdViewHandler(private val context: Context) {
    fun handleLink(ad: Ad) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.parse(ad.actionPath)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            HttpErrorTracker.trackHttpError(
                e.cause?.toString() ?: e.toString(),
                e.message ?: "handleLink failed for ad ${ad.id}",
                EventStrings.AD_ACTION_PATH_INVALID,
                ad.actionPath
            )
        }
    }

    fun handlePopup(ad: Ad) {
        try {
            val intent = AaWebViewPopupActivity().createActivity(context, ad)
            context.startActivity(intent)
        } catch (e: Exception) {
            HttpErrorTracker.trackHttpError(
                e.cause?.toString() ?: e.toString(),
                e.message ?: "handlePopup failed for ad ${ad.id}",
                EventStrings.AD_POPUP_FAILED,
                ad.actionPath
            )
        }
    }

    fun handleReportAd(adId: String, udid: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = buildReportAdUri(adId, udid)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            HttpErrorTracker.trackHttpError(
                e.cause?.toString() ?: e.toString(),
                e.message ?: "handleReportAd failed for adId $adId",
                EventStrings.AD_REPORT_FAILED,
                adId
            )
        }
    }

    private fun buildReportAdUri(adId: String, udid: String): Uri {
        return Uri.parse(Config.getAdReportingHost())
            .buildUpon()
            .appendQueryParameter(Config.AD_ID_PARAM, adId)
            .appendQueryParameter(Config.UDID_PARAM, udid)
            .build()
    }
}