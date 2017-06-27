package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.common.Interactor;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

public class InitializeSessionInteractor implements Interactor {
    private final InitializeSessionCommand command;
    private final SessionAdapter adapter;
    private final Callback callback;

    public InitializeSessionInteractor(final InitializeSessionCommand command,
                                       final SessionAdapter adapter,
                                       final Callback callback) {
        this.command = command;
        this.adapter = adapter;
        this.callback = callback;
    }

    @Override
    public void execute() {
        final JSONObject sessionRequest = command.getSessionRequest();
        adapter.sendInit(sessionRequest, new SessionAdapter.Callback() {
            @Override
            public void onSuccess(final Session session) {
                callback.onSessionInitialized(session);
            }

            @Override
            public void onFailure() {}
        });
    }

    public interface Callback {
        void onSessionInitialized(Session session);
    }
}
