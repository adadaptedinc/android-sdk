package com.adadapted.android.sdk.ext.json;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptBuilder;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 6/25/15.
 */
public class JsonKeywordInterceptBuilder implements KeywordInterceptBuilder {
    public KeywordIntercept build(JSONObject json) {
        return new KeywordIntercept("", 0L, parseAutofill(json), parseTrigger(json));
    }

    private Map<String, String> parseAutofill(JSONObject json) {
        Map<String, String> interceptMap = new HashMap<>();
        interceptMap.put(":Yogurt", "Chobani® Greek Yogurt");
        interceptMap.put(":Sour Cream", "Chobani® Greek Yogurt");
        interceptMap.put(":Mayonaise", "Chobani® Greek Yogurt");
        interceptMap.put(":Chobani® Greek Yogurt", "Chobani® Greek Yogurt");

        return interceptMap;
    }

    private HashMap<String, Ad> parseTrigger(JSONObject json) {
        return new HashMap<>();
    }
}
