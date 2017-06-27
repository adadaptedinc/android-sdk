package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.keywordintercept.model.KeywordInterceptEvent;

import org.json.JSONArray;

import java.util.Set;

public interface KeywordInterceptEventBuilder {
    JSONArray buildEvents(Set<KeywordInterceptEvent> events);
}
