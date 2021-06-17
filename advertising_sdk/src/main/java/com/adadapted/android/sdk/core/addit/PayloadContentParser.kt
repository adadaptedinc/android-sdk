package com.adadapted.android.sdk.core.addit

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.atl.AddToListItem
import com.adadapted.android.sdk.core.event.AppEventClient.Companion.getInstance
import org.json.JSONException
import org.json.JSONObject

class PayloadContentParser {

    fun parse(json: JSONObject?): List<AdditContent> {
        if (json == null) {
            return ArrayList()
        }
        val content: MutableList<AdditContent> = ArrayList()
        try {
            val payloads = json.getJSONArray(JsonFields.Payloads)
            if (payloads != null) {
                for (i in 0 until payloads.length()) {
                    val payload = payloads.getJSONObject(i)
                    if (payload != null) {
                        val parsed = parsePayload(payload)
                        content.add(parsed)
                    }
                }
            }
        } catch (ex: JSONException) {
            val errorParams: MutableMap<String, String> = HashMap()
            ex.message?.let { errorParams.put(EventStrings.EXCEPTION_MESSAGE, it) }
            getInstance().trackError(EventStrings.ADDIT_PAYLOAD_FIELD_PARSE_FAILED, "Problem parsing Payload JSON payload", errorParams)
        }
        return content
    }

    @Throws(JSONException::class)
    private fun parsePayload(json: JSONObject): AdditContent {
        val payloadId = if (json.has(JsonFields.PayloadId)) json.getString(JsonFields.PayloadId) else ""
        val message = if (json.has(JsonFields.PayloadMessage)) json.getString(JsonFields.PayloadMessage) else ""
        val image = if (json.has(JsonFields.PayloadImage)) json.getString(JsonFields.PayloadImage) else ""
        val payload: MutableList<AddToListItem> = ArrayList()

        if (json.has(JsonFields.DetailedListItems)) {
            val detailListItems = json.getJSONArray(JsonFields.DetailedListItems)
            for (i in 0 until detailListItems.length()) {
                val item = detailListItems[i] as JSONObject
                payload.add(parseItem(item))
            }
        } else {
            val errorParams: MutableMap<String, String> = HashMap()
            errorParams["payload_id"] = payloadId
            getInstance().trackError(EventStrings.ADDIT_PAYLOAD_FIELD_PARSE_FAILED, "Missing Detailed List Items.", errorParams)
        }
        return AdditContent.createPayloadContent(payloadId, message, image, ContentTypes.ADD_TO_LIST_ITEMS, payload)
    }

    private fun parseItem(itemJson: JSONObject): AddToListItem {
        val builder = AddToListItem.Builder()
        builder.setTrackingId(parseField(itemJson, JsonFields.TrackingId))
                .setTitle(parseField(itemJson, JsonFields.ProductTitle))
                .setBrand(parseField(itemJson, JsonFields.ProductBrand))
                .setCategory(parseField(itemJson, JsonFields.ProductCategory))
                .setProductUpc(parseField(itemJson, JsonFields.ProductBarCode))
                .setRetailerSku(parseField(itemJson, JsonFields.ProductSku))
                .setRetailerID(parseField(itemJson, JsonFields.ProductDiscount)) //discount to ID temp swap
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
            getInstance().trackError(EventStrings.ADDIT_PAYLOAD_FIELD_PARSE_FAILED, "Problem parsing Payload JSON input field $fieldName", errorParams)
            ""
        }
    }
}