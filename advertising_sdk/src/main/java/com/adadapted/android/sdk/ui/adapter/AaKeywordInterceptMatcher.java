package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.session.SessionClient;
import com.adadapted.android.sdk.core.keywordintercept.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.KeywordIntercept;
import com.adadapted.android.sdk.ui.model.SuggestionPayload;

import java.util.HashSet;
import java.util.Set;

public class AaKeywordInterceptMatcher implements SessionClient.Listener, KeywordInterceptClient.Listener {
    @SuppressWarnings("unused")
    private static final String LOGTAG = AaKeywordInterceptMatcher.class.getName();

    private final AaSuggestionTracker suggestionTracker;

    private KeywordIntercept keywordIntercept;
    private boolean mLoaded = false;
    private Session mSession;

    public AaKeywordInterceptMatcher() {
        suggestionTracker = new AaSuggestionTracker();

        SessionClient.getSession(this);
    }

    public SuggestionPayload match(final CharSequence constraint) {
        final Set<String> suggestions = new HashSet<>();

        if((isLoaded() && constraint != null && constraint.length() >= keywordIntercept.getMinMatchLength())) {
            for (final String item : keywordIntercept.getAutoFill().keySet()) {
                if (item != null && item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    final AutoFill autofill = keywordIntercept.getAutoFill().get(item);
                    if(autofill != null) {
                        suggestions.add(autofill.getReplacement());

                        suggestionTracker.suggestionMatched(
                            mSession,
                            item,
                            autofill.getReplacement(),
                            constraint.toString()
                        );
                    }
                }
            }
        }

        return new SuggestionPayload(suggestionTracker, suggestions);
    }

    public boolean suggestionSelected(final String suggestion) {
        return suggestionTracker.suggestionSelected(suggestion);
    }

    private boolean isLoaded() {
        return mLoaded;
    }

    @Override
    public void onKeywordInterceptInitialized(final KeywordIntercept keywordIntercept) {
        this.keywordIntercept = keywordIntercept;
        mLoaded = true;
    }

    @Override
    public void onSessionAvailable(final Session session) {
        mSession = session;
        KeywordInterceptClient.initialize(session, this);
    }

    @Override
    public void onAdsAvailable(Session session) {

    }

    @Override
    public void onSessionInitFailed() {

    }
}
