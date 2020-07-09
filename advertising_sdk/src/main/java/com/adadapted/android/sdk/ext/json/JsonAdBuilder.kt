package com.adadapted.android.sdk.ext.json

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.ad.AdActionType.handlesContent
import com.adadapted.android.sdk.core.ad.AdDisplayType
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonAdBuilder {
    fun buildAds(zoneId: String, jsonAds: JSONArray): List<Ad> {
        val ads: MutableList<Ad> = ArrayList()
        val adCount = jsonAds.length()
        for (i in 0 until adCount) {
            try {
                val ad = jsonAds.getJSONObject(i)
                if (ad.getString(TYPE) == AdDisplayType.HTML) {
                    ads.add(buildAd(zoneId, ad))
                } else {
                    getInstance().trackError(EventStrings.AD_PAYLOAD_PARSE_FAILED, "Ad " + ad.getString(AD_ID) + " has unsupported ad_type: " + ad.getString(TYPE))
                }
            } catch (ex: JSONException) {
                val errorParams: MutableMap<String, String> = HashMap()
                errorParams["bad_json"] = jsonAds.toString()
                errorParams["exception"] = ex.message.toString()
                getInstance().trackError(EventStrings.AD_PAYLOAD_PARSE_FAILED, "Problem parsing Ad JSON.", errorParams)
            }
        }
        return ads
    }

    @Throws(JSONException::class)
    fun buildAd(zoneId: String, ad: JSONObject): Ad {
        var parsedRefreshTime = DEFAULT_REFRESH_TIME
        try {
            parsedRefreshTime = ad.getString(REFRESH_TIME).toInt()
        } catch (ex: NumberFormatException) {
            getInstance().trackError(EventStrings.AD_PAYLOAD_PARSE_FAILED, "Ad " + ad[AD_ID] + " has an improperly set refresh_time.")
        }
        var payload: List<AddToListItem> = ArrayList()
        if (handlesContent(ad.getString(ACTION_TYPE))) {
            payload = parseAdContent(ad)
        }
        return Ad(
                ad.getString(AD_ID),
                zoneId,
                ad.getString(IMPRESSION_ID),
                ad.getString(CREATIVE_URL),
                ad.getString(ACTION_TYPE),
                ad.getString(ACTION_PATH),
                payload,
                parsedRefreshTime.toLong(),
                ad.getString(TRACKING_HTML))
    }

    @Throws(JSONException::class)
    private fun parseAdContent(ad: JSONObject): List<AddToListItem> {
        val payloadObject = ad.getJSONObject(PAYLOAD)
        return parseDetailedListItems(payloadObject.getJSONArray(CONTENT_DETAILED_LIST_ITEMS))
    }

    @Throws(JSONException::class)
    private fun parseDetailedListItems(items: JSONArray): List<AddToListItem> {
        val listItems: MutableList<AddToListItem> = ArrayList()
        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)
            val builder = AddToListItem.Builder()
            if (item.has(PRODUCT_TITLE)) {
                builder.setTitle(item.getString(PRODUCT_TITLE))
            } else {
                getInstance().trackError(EventStrings.SESSION_AD_PAYLOAD_PARSE_FAILED, "Detailed List Items payload should always have a product title.")
                break
            }
            if (item.has(PRODUCT_BRAND)) {
                builder.setBrand(item.getString(PRODUCT_BRAND))
            }
            if (item.has(PRODUCT_CATEGORY)) {
                builder.setCategory(item.getString(PRODUCT_CATEGORY))
            }
            if (item.has(PRODUCT_BARCODE)) {
                builder.setProductUpc(item.getString(PRODUCT_BARCODE))
            }
            if (item.has(PRODUCT_SKU)) {
                builder.setRetailerSku(item.getString(PRODUCT_SKU))
            }
            if (item.has(PRODUCT_DISCOUNT)) {
                builder.setDiscount(item.getString(PRODUCT_DISCOUNT))
            }
            if (item.has(PRODUCT_IMAGE)) {
                builder.setProductImage(item.getString(PRODUCT_IMAGE))
            }
            listItems.add(builder.build())
        }
        return listItems
    }

    companion object {
        private val LOGTAG = JsonAdBuilder::class.java.name
        private const val DEFAULT_REFRESH_TIME = 90
        private const val AD_ID = "ad_id"
        private const val IMPRESSION_ID = "impression_id"
        private const val REFRESH_TIME = "refresh_time"
        private const val HIDE_AFTER_INTERACTION = "hide_after_interaction"
        private const val TYPE = "type"
        private const val CREATIVE_URL = "creative_url"
        private const val ACTION_TYPE = "action_type"
        private const val ACTION_PATH = "action_path"
        private const val PAYLOAD = "payload"
        private const val POPUP = "popup"
        private const val TRACKING_HTML = "tracking_html"
        private const val SETTINGS = "settings"
        private const val CONTENT_DETAILED_LIST_ITEMS = "detailed_list_items"
        private const val PRODUCT_TITLE = "product_title"
        private const val PRODUCT_BRAND = "product_brand"
        private const val PRODUCT_CATEGORY = "product_category"
        private const val PRODUCT_BARCODE = "product_barcode"
        private const val PRODUCT_SKU = "product_sku"
        private const val PRODUCT_DISCOUNT = "product_discount"
        private const val PRODUCT_IMAGE = "product_image"
    }
}