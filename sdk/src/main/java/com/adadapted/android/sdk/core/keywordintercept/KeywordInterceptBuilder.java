package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 6/25/15.
 */
public interface KeywordInterceptBuilder {
    KeywordIntercept build(JSONObject object);
}
