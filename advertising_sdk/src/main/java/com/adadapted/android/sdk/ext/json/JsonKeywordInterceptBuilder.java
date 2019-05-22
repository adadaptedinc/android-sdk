package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonKeywordInterceptBuilder {
    private static final String TAG = JsonKeywordInterceptBuilder.class.getName();

    public KeywordIntercept build(final JSONObject json) {
        if(json == null) { return KeywordIntercept.empty(); }

        try {
            final String searchId = json.has(JsonFields.SEARCHID) ? json.getString(JsonFields.SEARCHID) : "";
            final long refreshTime = json.has(JsonFields.REFRESHTIME) ? Long.parseLong(json.getString(JsonFields.REFRESHTIME)) : 0L;
            final int minMatchLength = json.has(JsonFields.MINMATCHLENGTH) ? Integer.parseInt(json.getString(JsonFields.MINMATCHLENGTH)) : 2;

            final List<AutoFill> intercepts = sortAutoFills(parseAutoFills(json));

            Log.i(TAG, intercepts.toString());

            return new KeywordIntercept(searchId, refreshTime, minMatchLength, intercepts);
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

        return KeywordIntercept.empty();
    }

    private List<AutoFill> parseAutoFills(final JSONObject json) throws JSONException {
        final List<AutoFill> autoFills = new ArrayList<>();

        final Object obj = json.get(JsonFields.AUTOFILL);
        if(obj instanceof JSONObject) {
            final JSONObject autoFillJson = (JSONObject)obj;

            for(final Iterator<String> z = autoFillJson.keys(); z.hasNext();) {
                final String term = z.next();
                final JSONObject jsonTerm = autoFillJson.getJSONObject(term);

                final String termId = jsonTerm.has(JsonFields.TERMID) ?  jsonTerm.getString(JsonFields.TERMID) : "";
                final String replacement = jsonTerm.has(JsonFields.REPLACEMENT) ? jsonTerm.getString(JsonFields.REPLACEMENT) : "";
                final String icon = jsonTerm.has(JsonFields.ICON) ? jsonTerm.getString(JsonFields.ICON) : "";
                final String tagLine = jsonTerm.has(JsonFields.TAGLINE) ? jsonTerm.getString(JsonFields.TAGLINE) : "";
                final int priority = jsonTerm.has(JsonFields.PRIORITY) ? jsonTerm.getInt(JsonFields.PRIORITY) : 0;

                autoFills.add(new AutoFill(termId, term, replacement, icon, tagLine, priority));
            }
        }

        return autoFills;
    }

    private List<AutoFill> sortAutoFills(final List<AutoFill> autoFills) {
        final AutoFill[] arr = new AutoFill[autoFills.size()];
        autoFills.toArray(arr);
        final int size = arr.length;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if (arr[i].compareTo(arr[j]) < 1) {
                    final AutoFill temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        return Arrays.asList(arr);
    }
}
