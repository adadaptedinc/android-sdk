package com.adadapted.sdk.addit.core.common;

/**
 * Created by chrisweeden on 9/26/16.
 */

public interface InteractorExecuter {
    void executeInBackground(Interactor interactor);
    void executeOnMain(Interactor interactor);
}