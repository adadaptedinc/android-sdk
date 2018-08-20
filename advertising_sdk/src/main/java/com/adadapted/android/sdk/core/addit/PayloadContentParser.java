package com.adadapted.android.sdk.core.addit;

import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.event.AppEventClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayloadContentParser {
    public List<AdditContent> parse(final JSONObject json) {
        final List<AdditContent> content = new ArrayList<>();

        if(json != null) {
            try {
                final JSONArray payloads = json.getJSONArray(JsonFields.Payloads);
                for(int i = 0; i < payloads.length(); i++) {
                    content.add(parsePayload(payloads.getJSONObject(i)));
                }
            } catch (JSONException ex) {
                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("exception_message", ex.getMessage());
                AppEventClient.trackError(
                    "ADDIT_PAYLOAD_FIELD_PARSE_FAILED",
                    "Problem parsing Addit JSON payload",
                    errorParams
                );
            }
        }

        return content;
    }

    private AdditContent parsePayload(final JSONObject json) throws JSONException {
        final String payloadId = json.has(JsonFields.PayloadId) ? json.getString(JsonFields.PayloadId) : "";
        final String message = json.has(JsonFields.PayloadMessage) ? json.getString(JsonFields.PayloadMessage) : "";
        final String image = json.has(JsonFields.PayloadImage) ? json.getString(JsonFields.PayloadImage) : "";

        final List<AddToListItem> payload = new ArrayList<>();

        if(json.has(JsonFields.DetailedListItems)) {
            final JSONArray detailListItems = json.getJSONArray(JsonFields.DetailedListItems);
            for (int i = 0; i < detailListItems.length(); i++) {
                final JSONObject item = (JSONObject) detailListItems.get(i);
                payload.add(parseItem(item));
            }
        }
        else {
            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("payload_id", payloadId);
            AppEventClient.trackError(
                "ADDIT_PAYLOAD_FIELD_PARSE_FAILED",
                "Missing Detailed List Items.",
                errorParams
            );
        }

        return AdditContent.createPayloadContent(payloadId, message, image, ContentTypes.ADD_TO_LIST_ITEMS, payload);
    }

    private AddToListItem parseItem(final JSONObject itemJson) {
        final String trackingId = parseField(itemJson, JsonFields.TrackingId);
        final String title = parseField(itemJson, JsonFields.ProductTitle);
        final String brand = parseField(itemJson, JsonFields.ProductBrand);
        final String category = parseField(itemJson, JsonFields.ProductCategory);
        final String barCode = parseField(itemJson, JsonFields.ProductBarCode);
        final String discount = parseField(itemJson, JsonFields.ProductDiscount);
        final String productImage = parseField(itemJson, JsonFields.ProductImage);

        return new AddToListItem(trackingId, title, brand, category, barCode, discount, productImage);
    }

    private String parseField(final JSONObject itemJson, final String fieldName) {
        try {
            return itemJson.getString(fieldName);
        }
        catch(JSONException ex) {
            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("exception_message", ex.getMessage());
            errorParams.put("field_name", fieldName);
            AppEventClient.trackError(
                "ADDIT_PAYLOAD_FIELD_PARSE_FAILED",
                "Problem parsing Addit JSON input field " + fieldName,
                errorParams
            );

            return "";
        }
    }
}
