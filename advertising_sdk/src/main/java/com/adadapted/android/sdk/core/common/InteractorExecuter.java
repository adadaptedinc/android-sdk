package com.adadapted.android.sdk.core.common;

public interface InteractorExecuter {
    void executeInBackground(Interactor interactor);
    void executeOnMain(Interactor interactor);
}
