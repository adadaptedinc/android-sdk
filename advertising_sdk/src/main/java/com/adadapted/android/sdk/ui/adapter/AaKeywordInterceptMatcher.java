package com.adadapted.android.sdk.ui.adapter;

import com.adadapted.android.sdk.ext.management.KeywordInterceptManager;
import com.adadapted.android.sdk.core.keywordintercept.model.AutoFill;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.management.SessionManager;
import com.adadapted.android.sdk.ui.model.SuggestionPayload;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 6/25/15
 */
public class AaKeywordInterceptMatcher implements SessionManager.Callback, KeywordInterceptManager.Callback {
    private static final String LOGTAG = AaKeywordInterceptMatcher.class.getName();

    private final AaSuggestionTracker mSuggestionTracker;

    private KeywordIntercept mKeywordIntercept;
    private boolean mLoaded = false;
    private Session mSession;

    public AaKeywordInterceptMatcher() {
        mSuggestionTracker = new AaSuggestionTracker();

        SessionManager.getSession(this);
    }

    public SuggestionPayload match(final CharSequence constraint) {
        final Set<String> suggestions = new HashSet<>();

        if((isLoaded() && constraint != null && constraint.length() >= mKeywordIntercept.getMinMatchLength())) {
            for (final String item : mKeywordIntercept.getAutoFill().keySet()) {
                if (item != null && item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    final AutoFill autofill = mKeywordIntercept.getAutoFill().get(item);
                    if(autofill != null) {
                        suggestions.add(autofill.getReplacement());

                        mSuggestionTracker.suggestionMatched(
                            mSession,
                            item,
                            autofill.getReplacement(),
                            constraint.toString()
                        );
                    }
                }
            }
        }

        return new SuggestionPayload(mSuggestionTracker, suggestions);
    }

    public boolean suggestionSelected(final String suggestion) {
        return mSuggestionTracker.suggestionSelected(suggestion);
    }

    private boolean isLoaded() {
        return mLoaded;
    }

    @Override
    public void onKeywordInterceptInitSuccess(final KeywordIntercept keywordIntercept) {
        mKeywordIntercept = keywordIntercept;
        mLoaded = true;
    }

    @Override
    public void onSessionAvailable(final Session session) {
        mSession = session;
        KeywordInterceptManager.initialize(this);
    }

    @Override
    public void onNewAdsAvailable(final Session session) {}
}
