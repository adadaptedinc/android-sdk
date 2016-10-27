package com.adadapted.sdk.addit.core.content;

import android.app.Activity;
import android.net.Uri;
import android.util.Base64;

import com.adadapted.sdk.addit.core.app.AppEventSource;
import com.adadapted.sdk.addit.ext.factory.AppErrorTrackingManager;
import com.adadapted.sdk.addit.ext.factory.AppEventTrackingManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisweeden on 10/26/16.
 */
public class ContentPayloadParser {
    private static final String LOGTAG = ContentPayloadParser.class.getName();

    private final Activity activity;

    public ContentPayloadParser(final Activity activity) {
        this.activity = activity;
    }

    public AdditContent parse(final Uri uri) throws Exception {
        if(uri == null) {
            AppErrorTrackingManager.registerEvent(
                    "ADDIT_NO_DEEPLINK_RECEIVED",
                    "Did not receive a deeplink url.",
                    new HashMap<String, String>());

            throw new Exception("Did not receive a deeplink url.");
        }

        final Map<String, String> params = new HashMap<>();
        params.put("url", uri.toString());
        AppEventTrackingManager.registerEvent(
                AppEventSource.SDK,
                "deeplink_url_received",
                params);

        final String data = uri.getQueryParameter("data");
        final byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
        final String jsonString = new String(decodedData);

        try {
            final List<AdditAddToListItem> payload = new ArrayList<>();

            if(uri.getPath().endsWith("addit_add_list_items")) {
                final JSONObject jsonObject = new JSONObject(jsonString);
                final JSONArray detailListItems = jsonObject.getJSONArray("detailed_list_items");

                for(int i = 0; i < detailListItems.length(); i++) {
                    final JSONObject item = (JSONObject) detailListItems.get(i);
                    payload.add(parseItem(item));
                }

                return new AdditContent(activity, AdditContent.ADD_TO_LIST_ITEMS, payload);
            }
            else if (uri.getPath().endsWith("addit_add_list_item")) {
                final JSONObject jsonObject = new JSONObject(jsonString);
                final JSONObject detailListItem = jsonObject.getJSONObject("detailed_list_item");

                payload.add(parseItem(detailListItem));

                return new AdditContent(activity, AdditContent.ADD_TO_LIST_ITEM, payload);
            }

            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("url", uri.toString());
            AppErrorTrackingManager.registerEvent(
                    "ADDIT_UNKNOWN_PAYLOAD_TYPE",
                    "Unknown payload type: " + uri.getPath(),
                    errorParams);

            throw new Exception("Unknown payload type");
        }
        catch(JSONException ex) {
            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("payload", "{\"raw\":\""+data+"\", \"parsed\":\""+jsonString+"\"}");
            errorParams.put("exception_message", ex.getMessage());
            AppErrorTrackingManager.registerEvent(
                    "ADDIT_PAYLOAD_PARSE_FAILED",
                    "Problem parsing Addit JSON input",
                    errorParams);

            throw new Exception("Problem parsing content payload");
        }
    }

    private AdditAddToListItem parseItem(final JSONObject itemJson) {
        final String trackingId = parseField(itemJson, "tracking_id");
        final String title = parseField(itemJson, "product_title");
        final String brand = parseField(itemJson, "product_brand");
        final String category = parseField(itemJson, "product_category");
        final String barCode = parseField(itemJson, "product_barcode");
        final String discount = parseField(itemJson, "product_discount");
        final String productImage = parseField(itemJson, "product_image");

        return new AdditAddToListItem(trackingId, title, brand, category, barCode, discount, productImage);
    }

    private String parseField(final JSONObject itemJson, final String fieldName) {
        try {
            return itemJson.getString(fieldName);
        }
        catch(JSONException ex) {
            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("exception_message", ex.getMessage());
            errorParams.put("field_name", fieldName);
            AppErrorTrackingManager.registerEvent(
                    "ADDIT_PAYLOAD_FIELD_PARSE_FAILED",
                    "Problem parsing Addit JSON input field " + fieldName,
                    errorParams);

            return "";
        }
    }
}
