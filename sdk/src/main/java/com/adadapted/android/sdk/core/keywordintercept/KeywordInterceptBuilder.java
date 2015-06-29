package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;

/**
 * Created by chrisweeden on 6/25/15.
 */
public interface KeywordInterceptBuilder<T> {
    KeywordIntercept build(T object);
}
