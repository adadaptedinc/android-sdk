package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptBuilder;
import com.adadapted.android.sdk.core.keywordintercept.model.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.ext.management.AppEventTrackingManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chrisweeden on 6/25/15.
 */
public class JsonKeywordInterceptBuilder implements KeywordInterceptBuilder {
    private static final String TAG = JsonKeywordInterceptBuilder.class.getName();

    public KeywordIntercept build(final JSONObject json) {
        String searchId = "";
        long refreshTime = 0L;
        int minMatchLength = 2;

        try {
            if(json != null) {
                if (json.has(JsonFields.SEARCHID)) {
                    searchId = json.getString(JsonFields.SEARCHID);
                }

                if (json.has(JsonFields.REFRESHTIME)) {
                    refreshTime = Long.parseLong(json.getString(JsonFields.REFRESHTIME));
                }

                if (json.has(JsonFields.MINMATCHLENGTH)) {
                    minMatchLength = Integer.parseInt(json.getString(JsonFields.MINMATCHLENGTH));
                }
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem parsing JSON", ex);

            final Map<String, String> params = new HashMap<>();
            params.put("error", ex.getMessage());
            params.put("payload", json.toString());
            AppEventTrackingManager.registerEvent(
                    "KI_PAYLOAD_PARSE_FAILED",
                    "Failed to parse KI payload for processing.",
                    params);
        }

        final Map<String, AutoFill> interceptMap = parseAutofill(json);

        return new KeywordIntercept(searchId, refreshTime, minMatchLength, interceptMap);
    }

    private Map<String, AutoFill> parseAutofill(final JSONObject json) {
        final Map<String, AutoFill> interceptMap = new HashMap<>();

        try {
            if(json != null && json.has(JsonFields.AUTOFILL)) {
                Object obj = json.get(JsonFields.AUTOFILL);
                if(obj instanceof JSONObject) {
                    final JSONObject autofillJson = (JSONObject)obj;

                    for(final Iterator<String> z = autofillJson.keys(); z.hasNext();) {
                        final String term = z.next();
                        final JSONObject jsonTerm = autofillJson.getJSONObject(term);

                        String replacement = "";
                        String icon = "";
                        String tagline = "";

                        if (jsonTerm.has(JsonFields.REPLACEMENT)) {
                            replacement = jsonTerm.getString(JsonFields.REPLACEMENT);
                        }

                        if (jsonTerm.has(JsonFields.ICON)) {
                            icon = jsonTerm.getString(JsonFields.ICON);
                        }

                        if (jsonTerm.has(JsonFields.TAGLINE)) {
                            tagline = jsonTerm.getString(JsonFields.TAGLINE);
                        }

                        final AutoFill autofill = new AutoFill(replacement, icon, tagline);

                        interceptMap.put(term, autofill);
                    }
                }
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem parsing JSON", ex);

            final Map<String, String> params = new HashMap<>();
            params.put("exception", ex.getMessage());
            params.put("bad_json", json.toString());
            AppEventTrackingManager.registerEvent(
                    "KI_PAYLOAD_PARSE_FAILED",
                    "Failed to parse KI payload for processing.",
                    params);
        }

        return interceptMap;
    }
}
