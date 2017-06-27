package com.adadapted.android.sdk.core.common;

public interface PositiveListener<T> {
    void onSuccess(T object);
    void onFailure();
}
