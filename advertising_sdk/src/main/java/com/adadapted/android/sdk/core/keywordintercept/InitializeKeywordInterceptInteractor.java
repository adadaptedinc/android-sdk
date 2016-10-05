package com.adadapted.android.sdk.core.keywordintercept;

import android.util.Log;

import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;

/**
 * Created by chrisweeden on 9/29/16.
 */
public class InitializeKeywordInterceptInteractor implements Interactor {
    private static final String LOGTAG = InitializeKeywordInterceptInteractor.class.getName();

    private final InitializeKeywordInterceptCommand command;
    private final KeywordInterceptAdapter adapter;
    private final Callback callback;

    public InitializeKeywordInterceptInteractor(final InitializeKeywordInterceptCommand command,
                                                final KeywordInterceptAdapter adapter,
                                                final Callback callback) {
        this.command = command;
        this.adapter = adapter;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if(adapter == null) {
            Log.w(LOGTAG, "Provided Adapter is NULL");
            return;
        }

        if(command == null) {
            Log.w(LOGTAG, "Provided Command is NULL");
            return;
        }

        adapter.init(command.getKeywordInterceptRequest(), new KeywordInterceptAdapter.Callback() {
            @Override
            public void onSuccess(final KeywordIntercept keywordIntercept) {
                callback.onKeywordInterceptSessionInitialized(keywordIntercept);
            }

            @Override
            public void onFailure() {}
        });
    }

    public interface Callback {
        void onKeywordInterceptSessionInitialized(KeywordIntercept keywordIntercept);
    }
}
