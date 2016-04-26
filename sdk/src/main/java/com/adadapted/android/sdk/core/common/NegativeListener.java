package com.adadapted.android.sdk.core.common;

/**
 * Created by chrisweeden on 8/18/15.
 */
public interface NegativeListener<T> {
    void onSuccess();
    void onFailure(T object);
}
