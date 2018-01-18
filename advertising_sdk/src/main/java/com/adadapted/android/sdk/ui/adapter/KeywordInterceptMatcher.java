package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.core.keywordintercept.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;
import com.adadapted.android.sdk.ui.model.Suggestion;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeywordInterceptMatcher implements SessionClient.Listener, KeywordInterceptClient.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = KeywordInterceptMatcher.class.getName();

    private final Lock interceptLock = new ReentrantLock();
    private KeywordIntercept keywordIntercept;
    private boolean mLoaded = false;
    private Session mSession;

    public KeywordInterceptMatcher() {
        SessionClient.getSession(this);
    }

    public Set<Suggestion> match(final CharSequence constraint) {
        final Set<Suggestion> suggestions = new HashSet<>();

        interceptLock.lock();
        try {
            if((isLoaded() && constraint != null && constraint.length() >= keywordIntercept.getMinMatchLength())) {
                for (final String item : keywordIntercept.getAutoFill().keySet()) {
                    if (item != null && item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        final AutoFill autoFill = keywordIntercept.getAutoFill().get(item);
                        if(autoFill != null) {
                            suggestions.add(new Suggestion(keywordIntercept.getSearchId(), autoFill));

                            SuggestionTracker.suggestionMatched(
                                mSession,
                                keywordIntercept.getSearchId(),
                                item,
                                autoFill.getReplacement(),
                                constraint.toString()
                            );
                        }
                    }
                }
            }
        }
        finally {
            interceptLock.unlock();
        }

        return suggestions;
    }

    public void suggestionPresented(final String suggestion) {
        interceptLock.lock();
        try {
            SuggestionTracker.suggestionPresented(keywordIntercept.getSearchId(), suggestion);
        }
            finally {
            interceptLock.unlock();
        }
    }

    public void suggestionSelected(final String suggestion) {
        interceptLock.lock();
        try {
            SuggestionTracker.suggestionSelected(keywordIntercept.getSearchId(), suggestion);
        }
        finally {
            interceptLock.unlock();
        }
    }

    private boolean isLoaded() {
        return mLoaded;
    }

    @Override
    public void onKeywordInterceptInitialized(final KeywordIntercept keywordIntercept) {
        AppEventClient.trackAppEvent("ki_initialized");

        interceptLock.lock();
        try {
            this.keywordIntercept = keywordIntercept;
            mLoaded = true;
        }
        finally {
            interceptLock.unlock();
        }
    }

    @Override
    public void onSessionAvailable(final Session session) {
        interceptLock.lock();
        try {
            mSession = session;
        }
        finally {
            interceptLock.unlock();
        }

        KeywordInterceptClient.initialize(session, this);
    }

    @Override
    public void onAdsAvailable(final Session session) {
        interceptLock.lock();
        try {
            mSession = session;
        }
        finally {
            interceptLock.unlock();
        }

        KeywordInterceptClient.initialize(session, this);
    }

    @Override
    public void onSessionInitFailed() {}
}
