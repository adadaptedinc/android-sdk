package com.adadapted.android.sdk.core.addit

import android.net.Uri
import android.util.Base64
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DeeplinkContentParser {
    @Throws(Exception::class)
    fun parse(uri: Uri?): AdditContent {
        if (uri == null) {
            getInstance().trackError(
                    EventStrings.ADDIT_NO_DEEPLINK_RECEIVED,
                    "Did not receive a deeplink url."
            )
            throw Exception("Did not receive a deeplink url.")
        }
        val data = uri.getQueryParameter("data")
        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val jsonString = String(decodedData)
        try {
            val payload: MutableList<AddToListItem> = ArrayList()
            val jsonObject = JSONObject(jsonString)
            val payloadId = if (jsonObject.has(JsonFields.PayloadId)) jsonObject.getString(JsonFields.PayloadId) else ""
            val message = if (jsonObject.has(JsonFields.PayloadMessage)) jsonObject.getString(JsonFields.PayloadMessage) else ""
            val image = if (jsonObject.has(JsonFields.PayloadImage)) jsonObject.getString(JsonFields.PayloadImage) else ""
            val urlPath = uri.path
            if (urlPath != null && urlPath.endsWith("addit_add_list_items")) {
                val detailListItems = jsonObject.getJSONArray(JsonFields.DetailedListItems)
                for (i in 0 until detailListItems.length()) {
                    val item = detailListItems[i] as JSONObject
                    payload.add(parseItem(item))
                }
                return AdditContent.createDeeplinkContent(payloadId, message, image, ContentTypes.ADD_TO_LIST_ITEMS, payload)
            } else if (urlPath != null && urlPath.endsWith("addit_add_list_item")) {
                val detailListItem = jsonObject.getJSONObject(JsonFields.DetailedListItem)
                payload.add(parseItem(detailListItem))
                return AdditContent.createDeeplinkContent(payloadId, message, image, ContentTypes.ADD_TO_LIST_ITEM, payload)
            }
            val errorParams: MutableMap<String, String> = HashMap()
            errorParams["url"] = uri.toString()
            getInstance().trackError(
                    EventStrings.ADDIT_UNKNOWN_PAYLOAD_TYPE,
                    "Unknown payload type: " + uri.path,
                    errorParams
            )
            throw Exception("Unknown payload type")
        } catch (ex: JSONException) {
            val errorParams: MutableMap<String, String> = HashMap()
            errorParams["payload"] = "{\"raw\":\"$data\", \"parsed\":\"$jsonString\"}"
            ex.message?.let { errorParams.put(EventStrings.EXCEPTION_MESSAGE, it) }
            getInstance().trackError(
                    EventStrings.ADDIT_PAYLOAD_PARSE_FAILED,
                    "Problem parsing Deeplink JSON input",
                    errorParams
            )
            throw Exception("Problem parsing content payload")
        }
    }

    private fun parseItem(itemJson: JSONObject): AddToListItem {
        val builder = AddToListItem.Builder()
        builder.setTrackingId(parseField(itemJson, JsonFields.TrackingId))
                .setTitle(parseField(itemJson, JsonFields.ProductTitle))
                .setBrand(parseField(itemJson, JsonFields.ProductBrand))
                .setCategory(parseField(itemJson, JsonFields.ProductCategory))
                .setProductUpc(parseField(itemJson, JsonFields.ProductBarCode))
                .setRetailerSku(parseField(itemJson, JsonFields.ProductSku))
                .setDiscount(parseField(itemJson, JsonFields.ProductDiscount))
                .setProductImage(parseField(itemJson, JsonFields.ProductImage))
        return builder.build()
    }

    private fun parseField(itemJson: JSONObject, fieldName: String): String {
        return try {
            itemJson.getString(fieldName)
        } catch (ex: JSONException) {
            val errorParams: MutableMap<String, String> = HashMap()
            ex.message?.let { errorParams.put(EventStrings.EXCEPTION_MESSAGE, it) }
            errorParams["field_name"] = fieldName
            getInstance().trackError(
                    EventStrings.ADDIT_PAYLOAD_FIELD_PARSE_FAILED,
                    "Problem parsing Deeplink JSON input field $fieldName",
                    errorParams
            )
            ""
        }
    }

    companion object {
        private val LOGTAG = DeeplinkContentParser::class.java.name
    }
}