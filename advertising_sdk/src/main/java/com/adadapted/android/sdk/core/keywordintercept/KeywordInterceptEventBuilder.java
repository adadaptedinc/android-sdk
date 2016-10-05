package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.keywordintercept.model.KeywordInterceptEvent;

import org.json.JSONArray;

import java.util.Set;

/**
 * Created by chrisweeden on 10/3/16.
 */

public interface KeywordInterceptEventBuilder {
    JSONArray buildEvents(Set<KeywordInterceptEvent> events);
}
