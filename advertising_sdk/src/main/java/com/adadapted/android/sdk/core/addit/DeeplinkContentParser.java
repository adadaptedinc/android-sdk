package com.adadapted.android.sdk.core.addit;

import android.net.Uri;
import android.util.Base64;

import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.event.AppEventClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeeplinkContentParser {
    @SuppressWarnings("unused")
    private static final String LOGTAG = DeeplinkContentParser.class.getName();

    public AdditContent parse(final Uri uri) throws Exception {
        if(uri == null) {
            AppEventClient.trackError(
                "ADDIT_NO_DEEPLINK_RECEIVED",
                "Did not receive a deeplink url."
            );

            throw new Exception("Did not receive a deeplink url.");
        }

        final Map<String, String> params = new HashMap<>();
        params.put("url", uri.toString());
        AppEventClient.trackSdkEvent(
            "deeplink_url_received",
            params
        );

        final String data = uri.getQueryParameter("data");
        final byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
        final String jsonString = new String(decodedData);

        try {
            final List<AddToListItem> payload = new ArrayList<>();

            final JSONObject jsonObject = new JSONObject(jsonString);
            final String payloadId = jsonObject.has(JsonFields.PayloadId) ? jsonObject.getString(JsonFields.PayloadId) : "";
            final String message = jsonObject.has(JsonFields.PayloadMessage) ? jsonObject.getString(JsonFields.PayloadMessage) : "";
            final String image = jsonObject.has(JsonFields.PayloadImage) ? jsonObject.getString(JsonFields.PayloadImage) : "";

            if(uri.getPath().endsWith("addit_add_list_items")) {
                final JSONArray detailListItems = jsonObject.getJSONArray(JsonFields.DetailedListItems);

                for(int i = 0; i < detailListItems.length(); i++) {
                    final JSONObject item = (JSONObject) detailListItems.get(i);
                    payload.add(parseItem(item));
                }

                return AdditContent.createDeeplinkContent(payloadId, message, image, ContentTypes.ADD_TO_LIST_ITEMS, payload);
            }
            else if (uri.getPath().endsWith("addit_add_list_item")) {
                final JSONObject detailListItem = jsonObject.getJSONObject(JsonFields.DetailedListItem);

                payload.add(parseItem(detailListItem));

                return AdditContent.createDeeplinkContent(payloadId, message, image, ContentTypes.ADD_TO_LIST_ITEM, payload);
            }

            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("url", uri.toString());
            AppEventClient.trackError(
                "ADDIT_UNKNOWN_PAYLOAD_TYPE",
                "Unknown payload type: " + uri.getPath(),
                errorParams
            );

            throw new Exception("Unknown payload type");
        }
        catch(JSONException ex) {
            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("payload", "{\"raw\":\""+data+"\", \"parsed\":\""+jsonString+"\"}");
            errorParams.put("exception_message", ex.getMessage());
            AppEventClient.trackError(
                "ADDIT_PAYLOAD_PARSE_FAILED",
                "Problem parsing Addit JSON input",
                errorParams
            );

            throw new Exception("Problem parsing content payload");
        }
    }

    private AddToListItem parseItem(final JSONObject itemJson) {
        final AddToListItem.Builder builder = new AddToListItem.Builder();
        builder.setTrackingId(parseField(itemJson, JsonFields.TrackingId))
                .setTitle(parseField(itemJson, JsonFields.ProductTitle))
                .setBrand(parseField(itemJson, JsonFields.ProductBrand))
                .setCategory(parseField(itemJson, JsonFields.ProductCategory))
                .setProductUpc(parseField(itemJson, JsonFields.ProductBarCode))
                .setRetailerSku(parseField(itemJson, JsonFields.ProductSku))
                .setDiscount(parseField(itemJson, JsonFields.ProductDiscount))
                .setProductImage(parseField(itemJson, JsonFields.ProductImage));

        return builder.build();
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
