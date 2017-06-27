package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;

import org.json.JSONObject;

public interface KeywordInterceptBuilder {
    KeywordIntercept build(JSONObject object);
}
