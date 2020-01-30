package com.adadapted.android.sdk.core.addit;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class PayloadContentParserTest {

    private final String JSON_STRING = "{\n" +
            "\t\"payloads\": [{\n" +
            "\t\t\"payload_id\": \"7498E63F-BF44-466B-A391-8B6721C32FF7\",\n" +
            "\t\t\"payload_message\": \"Sample Product\",\n" +
            "\t\t\"payload_image\": \"\",\n" +
            "\t\t\"campaign_id\": \"254\",\n" +
            "\t\t\"app_id\": \"droidrecipe\",\n" +
            "\t\t\"expire_seconds\": 604800,\n" +
            "\t\t\"detailed_list_items\": [{\n" +
            "\t\t\t\"tracking_id\": \"D0699892-BF9E-4E35-B92D-C5D7F2D6E9AD\",\n" +
            "\t\t\t\"product_title\": \"Sample Product\",\n" +
            "\t\t\t\"product_brand\": \"Brand\",\n" +
            "\t\t\t\"product_category\": \"\",\n" +
            "\t\t\t\"product_barcode\": \"0\",\n" +
            "\t\t\t\"product_sku\": \"\",\n" +
            "\t\t\t\"product_discount\": \"\",\n" +
            "\t\t\t\"product_image\": \"https:\\/\\/images.adadapted.com\\/\"\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}";

    @Before
    public void setUp() throws Exception { }

    @Test
    public void parse_null_Json_returns_none() throws JSONException {
        final PayloadContentParser parser = new PayloadContentParser();
        final List<AdditContent> content = parser.parse(null);

        assertEquals(0, content.size());
    }

    @Test
    public void parse() throws JSONException {
        final JSONObject json = new JSONObject(JSON_STRING);

        final PayloadContentParser parser = new PayloadContentParser();
        final List<AdditContent> content = parser.parse(json);

        assertEquals(1, content.size());
    }
}