package com.adadapted.android.sdk.core.common;

/**
 * Created by chrisweeden on 8/18/15.
 */
public interface PositiveListener<T> {
    void onSuccess(T object);
    void onFailure();
}
