package com.adadapted.android.sdk.core.session;

/**
 * Created by chrisweeden on 3/26/15.
 */
public interface SessionAdapter<T> {
    interface Listener<T> {
        void onSessionInitRequestCompleted(T response);
        void onSessionInitRequestFailed();
        void onSessionReinitRequestNoContent();
        void onSessionReinitRequestFailed();
    }

    void sendInit(T request);
    void sendReinit(T request);

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
