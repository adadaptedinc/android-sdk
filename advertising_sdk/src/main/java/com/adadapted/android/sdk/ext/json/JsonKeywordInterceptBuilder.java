package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.Suggestion;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonKeywordInterceptBuilder {
    private static final String TAG = JsonKeywordInterceptBuilder.class.getName();

    public KeywordIntercept build(final JSONObject json) {
        if(json == null) { return null; }

        try {
            final String searchId = json.has(JsonFields.SEARCHID) ? json.getString(JsonFields.SEARCHID) : "";
            final long refreshTime = json.has(JsonFields.REFRESHTIME) ? Long.parseLong(json.getString(JsonFields.REFRESHTIME)) : 0L;
            final int minMatchLength = json.has(JsonFields.MINMATCHLENGTH) ? Integer.parseInt(json.getString(JsonFields.MINMATCHLENGTH)) : 2;

            final Map<String, Suggestion> interceptMap = parseAutofill(json);

            return new KeywordIntercept(searchId, refreshTime, minMatchLength, interceptMap);
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem parsing JSON", ex);

            final Map<String, String> params = new HashMap<>();
            params.put("error", ex.getMessage());
            params.put("payload", json.toString());
            AppEventClient.trackError(
                "KI_PAYLOAD_PARSE_FAILED",
                "Failed to parse KI payload for processing.",
                params
            );
        }

        return null;
    }

    private Map<String, Suggestion> parseAutofill(final JSONObject json) throws JSONException {
        final Map<String, Suggestion> interceptMap = new HashMap<>();

        final Object obj = json.get(JsonFields.AUTOFILL);
        if(obj instanceof JSONObject) {
            final JSONObject autofillJson = (JSONObject)obj;

            for(final Iterator<String> z = autofillJson.keys(); z.hasNext();) {
                final String term = z.next();
                final JSONObject jsonTerm = autofillJson.getJSONObject(term);

                final String replacement = jsonTerm.has(JsonFields.REPLACEMENT) ? jsonTerm.getString(JsonFields.REPLACEMENT) : "";
                final String icon = jsonTerm.has(JsonFields.ICON) ? jsonTerm.getString(JsonFields.ICON) : "";
                final String tagline = jsonTerm.has(JsonFields.TAGLINE) ? jsonTerm.getString(JsonFields.TAGLINE) : "";

                final Suggestion autofill = new Suggestion(replacement, icon, tagline);

                interceptMap.put(term, autofill);
            }
        }

        return interceptMap;
    }
}
