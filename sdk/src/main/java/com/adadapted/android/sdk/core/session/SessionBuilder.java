package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.session.model.Session;

/**
 * Created by chrisweeden on 3/23/15.
 */
public interface SessionBuilder<T> {
    Session buildSession(T response);
}
