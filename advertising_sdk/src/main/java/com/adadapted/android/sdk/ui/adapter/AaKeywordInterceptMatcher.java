package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.core.keywordintercept.Suggestion;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;
import com.adadapted.android.sdk.ui.model.SuggestionPayload;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AaKeywordInterceptMatcher implements SessionClient.Listener, KeywordInterceptClient.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AaKeywordInterceptMatcher.class.getName();

    private final AaSuggestionTracker suggestionTracker;

    private final Lock interceptLock = new ReentrantLock();
    private KeywordIntercept keywordIntercept;
    private boolean mLoaded = false;
    private Session mSession;

    public AaKeywordInterceptMatcher() {
        suggestionTracker = new AaSuggestionTracker();

        SessionClient.getSession(this);
    }

    public SuggestionPayload match(final CharSequence constraint) {
        final Set<Suggestion> suggestions = new HashSet<>();
        String searchId = "";

        interceptLock.lock();
        try {
            searchId = keywordIntercept.getSearchId();
            if((isLoaded() && constraint != null && constraint.length() >= keywordIntercept.getMinMatchLength())) {
                for (final String item : keywordIntercept.getAutoFill().keySet()) {
                    if (item != null && item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        final Suggestion suggestion = keywordIntercept.getAutoFill().get(item);
                        if(suggestion != null) {
                            suggestions.add(suggestion);

                            suggestionTracker.suggestionMatched(
                                mSession,
                                keywordIntercept.getSearchId(),
                                item,
                                suggestion.getReplacement(),
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

        return new SuggestionPayload(searchId, suggestionTracker, suggestions);
    }

    public void suggestionPresented(final String suggestion) {
        interceptLock.lock();
        try {
            suggestionTracker.suggestionPresented(keywordIntercept.getSearchId(), suggestion);
        }
            finally {
            interceptLock.unlock();
        }
    }

    public boolean suggestionSelected(final String suggestion) {
        interceptLock.lock();
        try {
            return suggestionTracker.suggestionSelected(keywordIntercept.getSearchId(), suggestion);
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
