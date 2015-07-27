package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptBuilder;
import com.adadapted.android.sdk.core.keywordintercept.model.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;

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

    public KeywordIntercept build(JSONObject json) {
        String searchId = "";
        long refreshTime = 0L;
        int minMatchLength = 2;

        try {
            if (json.has("search_id")) {
                searchId = json.getString("search_id");
            }

            if (json.has("refresh_time")) {
                refreshTime = Long.parseLong(json.getString("refresh_time"));
            }

            if (json.has("min_match_length")) {
                minMatchLength = Integer.parseInt(json.getString("min_match_length"));
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem parsing JSON", ex);
        }

        Map<String, AutoFill> interceptMap = parseAutofill(json);

        return new KeywordIntercept(searchId, refreshTime, minMatchLength, interceptMap);
    }

    private Map<String, AutoFill> parseAutofill(JSONObject json) {
        Map<String, AutoFill> interceptMap = new HashMap<>();

        try {
            if(json.has("autofill")) {
                JSONObject autofillJson = json.getJSONObject("autofill");
                for(Iterator<String> z = autofillJson.keys(); z.hasNext();) {
                    String term = z.next();
                    JSONObject jsonTerm = autofillJson.getJSONObject(term);

                    String replacement = "";
                    String icon = "";
                    String tagline = "";

                    if (jsonTerm.has("replacement")) {
                        replacement = jsonTerm.getString("replacement");
                    }

                    if (jsonTerm.has("icon")) {
                        icon = jsonTerm.getString("icon");
                    }

                    if (jsonTerm.has("tagline")) {
                        tagline = jsonTerm.getString("tagline");
                    }

                    AutoFill autofill = new AutoFill(replacement, icon, tagline);

                    interceptMap.put(term, autofill);
                }
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem parsing JSON", ex);
        }

        return interceptMap;
    }
}
