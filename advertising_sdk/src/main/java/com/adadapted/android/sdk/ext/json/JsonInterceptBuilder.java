package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.intercept.Term;
import com.adadapted.android.sdk.core.intercept.Intercept;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonInterceptBuilder {
    private static final String TAG = JsonInterceptBuilder.class.getName();

    private static final String SEARCH_ID = "search_id";
    private static final String REFRESH_TIME = "refresh_time";
    private static final String MIN_MATCH_LENGTH = "min_match_length";
    private static final String TERMS = "terms";

    private static final String TERM_ID = "term_id";
    private static final String TERM = "term";
    private static final String AUTO_FILL = "autofill";
    private static final String REPLACEMENT = "replacement";
    private static final String ICON = "icon";
    private static final String TAG_LINE = "tagline";
    private static final String PRIORITY = "priority";

    public Intercept build(final JSONObject json) {
        if(json == null) { return Intercept.empty(); }

        try {
            final String searchId = json.has(SEARCH_ID) ? json.getString(SEARCH_ID) : "";
            final long refreshTime = json.has(REFRESH_TIME) ? Long.parseLong(json.getString(REFRESH_TIME)) : 0L;
            final int minMatchLength = json.has(MIN_MATCH_LENGTH) ? Integer.parseInt(json.getString(MIN_MATCH_LENGTH)) : 2;
            final JSONArray terms = json.has(TERMS) ? json.getJSONArray(TERMS) : new JSONArray();

            final List<Term> intercepts = sortIntercepts(parseIntercepts(terms));

            return new Intercept(searchId, refreshTime, minMatchLength, intercepts);
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

        return Intercept.empty();
    }

    private List<Term> parseIntercepts(final JSONArray json) throws JSONException {
        final List<Term> terms = new ArrayList<>();

        for(int i = 0; i < json.length(); i++) {
            final JSONObject termJson = json.getJSONObject(i);

            final String termId = termJson.has(TERM_ID) ?  termJson.getString(TERM_ID) : "";
            final String term = termJson.has(TERM) ?  termJson.getString(TERM) : "";
            final String replacement = termJson.has(REPLACEMENT) ? termJson.getString(REPLACEMENT) : "";
            final String icon = termJson.has(ICON) ? termJson.getString(ICON) : "";
            final String tagLine = termJson.has(TAG_LINE) ? termJson.getString(TAG_LINE) : "";
            final int priority = termJson.has(PRIORITY) ? termJson.getInt(PRIORITY) : 0;

            terms.add(new Term(termId, term, replacement, icon, tagLine, priority));
        }

        return terms;
    }

    private List<Term> sortIntercepts(final List<Term> terms) {
        final Term[] arr = new Term[terms.size()];
        terms.toArray(arr);
        final int size = arr.length;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if (arr[i].compareTo(arr[j]) < 1) {
                    final Term temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        return Arrays.asList(arr);
    }
}
