package com.adadapted.android.sdk.core.keywordintercept;

/**
 * Created by chrisweeden on 6/23/15.
 */
public interface KeywordInterceptAdapter<T> {
    interface Listener<T> {
        void onInitSuccess(T object);
        void onInitFailed();
        void onTrackSuccess();
        void onTrackFailed();
    }

    void init(T request);
    void track(T request);

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
